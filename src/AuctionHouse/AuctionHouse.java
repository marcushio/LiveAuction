package AuctionHouse;

import Agent.Agent;
import Helper.*;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

/**Object Representing Auction House*/
public class AuctionHouse implements Runnable, AuctionHouseRemoteService{
    /**Unique Identification of this auction house*/
    private String ID;
    /**Max number of auction stages at once*/
    private int itemCount = 3;
    /**Object containing all items*/
    private Storage storage;
    private static String book = "C:/Items.txt";
    public Auction[] stages;
    private static int portNumber;
    private String accountNumber;
    private boolean balance;
    /**Queue of bid objects to process*/
    private BlockingQueue<Bid> external = new LinkedBlockingDeque<>();
    private static Scanner scanner = new Scanner(System.in);
    /**Stores the item sold*/
    private BankRemoteService bankService;
    private String bankName = "bankServer";
    private String bankIP = "127.0.0.1";
    private int bankPort;  //pretty sure we're just going to keep this the standard 1099 -marcus


    /**Constructs auction house with a unique ID*/
    public AuctionHouse(Storage storage){
        this.storage = storage;
        ID = UUID.randomUUID().toString();
        stages = new Auction[itemCount];
    }

    /**Return a list of items in this Auction*/
    public List<Item> getListedItems(){
        List<Item> items = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            items.add(this.stages[i].getItem());
        }
        return items;
    }

    /**Return the ID of this auction house*/
    public String getID(){
        return ID;
    }

    /**make the remote server*/
    private void initialize(){
        try {
            System.out.println("making Auction House "+ID+"...");
            AuctionHouseRemoteService thisServer = this;
            AuctionHouseRemoteService stub = (AuctionHouseRemoteService)
                    UnicastRemoteObject.exportObject((AuctionHouseRemoteService)thisServer, 0);
            System.out.println("Auction House made, now binding");
            Registry registry = LocateRegistry.createRegistry(portNumber);
            registry.rebind(ID, stub);
            System.out.println("Server created... server running...");
        } catch (RemoteException ex) {
            System.err.println("Remote exception while making a new Auction House.");
            System.out.println(ex.getMessage());
        }
        Auction temp;
        System.out.println("Auction House Offers:");
        for(int i = 0; i<itemCount;i++){
            temp = new Auction(storage.getRandomItem());
            stages[i] = temp;
            Thread t = new Thread(temp);
            t.start();
        }
    }

    /**Find the index of item
     * @param item Item object to look for
     * @return index of the item in items.*/
    public int findItem(Item item){
        for(int i = 0; i<itemCount;i++){
            if(item.equals(stages[i].getItem())){
                return i;
            }
        }
        return -1;
    }

    /**Check on all current auction*/
    private void checkOnAuctions(){
        Auction stage;
        Item item;
        int status;
        for(int i = 0; i<itemCount; i++){
            stage = stages[i];
            status = stage.getAuctionStatus();
            if( status != 0){
                if(status == 1){
                    notifyWinner(stage);
                }
                item = storage.getRandomItem();
                replaceStage(i,item);
            }
        }
    }

    private void replaceStage(int i, Item item){
        Auction temp = new Auction(storage.getRandomItem());
        stages[i] = temp;
        Thread t = new Thread(temp);
        t.start();
    }

    private void notifyWinner(Auction stage){
        Bid win = stage.getMaxBid();
        win.setStatus(BidStatusMessage.WINNER);
        /**Send this to agent*/
    }

    /**Remote method for agent to send a bid*/
    public void makeBid(Bid bid){
        try {
            external.put(bid);
        }catch(InterruptedException e){
            e.printStackTrace();
        }
    }

    /**Try process bid*/
    private void processBid() throws InterruptedException{
        Bid bid = external.take();
        if(bid != null) {
            Item i = bid.getItem();
            double price = bid.getPriceVal();
            int index = findItem(i);
            if (index > -1) {
                Bid currentBid = stages[index].getMaxBid();
                /**Check if the new bid amount is higher than current max bid*/
                if (price > currentBid.getBidAmount()) {
                    /**Request bank to check affordable*/
                    if (true) {
                        /**Inform current bidder
                         * outbid him with new bidder
                         * reset 60 sec counter*/
                        currentBid.setStatus(BidStatusMessage.OUTBID);
                        bid.setStatus(BidStatusMessage.ACCEPTED);
                    }
                }
            } else {
                bid.setStatus(BidStatusMessage.REJECTED);
            }
        }else{

        }
    }

    /**Register an account at bank with ID(Used as account ID?)*/
    private void registerAtBank(){
        //code below is to connect to the bank over RMI
        try {
            Registry rmiRegistry = LocateRegistry.getRegistry(bankIP);
            bankService = (BankRemoteService) rmiRegistry.lookup(bankName);  //this is for remote machines
            //bankService = (BankRemoteService) Naming.lookup("bankServer"); // -this was used when on same pc;
            accountNumber = bankService.registerAuctionHouse(InetAddress.getLocalHost().toString(), ID);
            //InetAddress.getLocalHost(); returns an InetAddress
            //InetAddress.getLocalHost().getHostAddress returns string...
        } catch(IOException e){
            e.printStackTrace();
        } catch(NotBoundException ex){
            ex.printStackTrace();
        }
        //

        /// getting auction's ip stuffs ////
//        InetAddress ip;
//        String hostname;
//        try{
//            ip = InetAddress.getLocalHost();
//            hostname = ip.getHostName();
//        } catch (UnknownHostException ex ){
//
//        }
        ///////
    }

    /**Deregister at bank*/
    private void deRegisterAtBank(){
        /**Deregister at bank and receive money in account*/
        try {
            bankService.deregister(accountNumber);
        } catch(RemoteException ex){
            ex.printStackTrace();
        }
    }

    @Override
    public void run() {
        /**First Thing register at bank*/
        registerAtBank();
        initialize();
        while(!Thread.interrupted()){
            try{
                if(!external.isEmpty()) {
                    processBid();
                }
                checkOnAuctions();
                /*if(!storage.isEmpty()){
                    removeStage();
                    addNewStage();
                }else{
                    System.out.println("Storage Empty, Auction House Closing!");
                    Thread.currentThread().interrupt();
                    break;
                }*/
                Thread.sleep(1000);
            }catch (InterruptedException e){
                e.printStackTrace();
            }
        }
        deRegisterAtBank();
    }

    /**Check if objects are equal by comparing IDs*/
    public boolean equals(AuctionHouse a){
        if(ID.equals(a.getID())){
            return true;
        }
        return false;
    }

    public String toString(){
        String s = "Auction House "+ID+"\n";
        for(int i = 0; i< itemCount; i++){
            s += stages[i].toString()+"\n";
        }
        return s;
    }

    /**Check if there are bids going on
     * @return true if we can deregister, else return false*/
    private boolean deregisterable(){
        Auction curr;
        for(int i = 0; i<itemCount;i++){
            curr = stages[i];
            if(curr != null) {
                /**Change this to check if there is bid going on*/
                if (curr.bidGoingOn()){
                    return false;
                }
            }
        }
        return true;
    }

    private void userInterface(){
        String response = scanner.next();
        if(response.equals("exit") || response.equals("x")){
            /**Try deregister at bank
             * if succeed, exit program
             * else recursively call exsit*/
            if(deregisterable()){
                Thread.currentThread().interrupt();

                System.exit(0);
            }else{
                userInterface();
            }
        }else{
            userInterface();
        }
    }

    public static void main(String args[]) throws IOException {
        if(args.length > 0) {
            portNumber = Integer.parseInt(args[0]);
            Storage storage = new Storage(book);
            storage.initialize();
            AuctionHouse auctionHouse = new AuctionHouse(storage);
            Thread t = new Thread(auctionHouse);
            t.start();
            auctionHouse.userInterface();
        }else{
            Storage storage = new Storage(book);
            storage.initialize();
            AuctionHouse auctionHouse = new AuctionHouse(storage);
            Thread t = new Thread(auctionHouse);
            t.start();
            auctionHouse.userInterface();
        }
    }

}
