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
    private static String book = "/nfs/student/m/marcustrujillo/Documents/auction/DistributedAuction/Resource";
    public Auction[] stages;
    private static int portNumber;
    private String accountNumber;
    private boolean balance;
    /**Queue of bid objects to process*/
    private BlockingQueue<Bid> external = new LinkedBlockingDeque<>();
    private static Scanner scanner = new Scanner(System.in);
    /**Stores the item sold*/
    private String ip;
    private String hostname;
    private BankRemoteService bankService;
    private AgentRemoteService agentService;
    private String bankName = "bankServer";
    private static String bankIP;
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

    public Item getItem(){
        return stages[0].getItem();
    }

    /**Return the ID of this auction house*/
    public String getID(){
        return ID;
    }

    /**make the remote server*/
    private void initialize(){
        try{
            InetAddress ipp = InetAddress.getLocalHost();
            String name = ipp.getHostName();
            InetAddress realIP = InetAddress.getByName(name);
            hostname = ID;
            System.out.println("Your current IP address: "+ip);
        }catch (UnknownHostException e){
            e.printStackTrace();
        }

        try {
            System.out.println("making Auction House "+ID+"...");
            AuctionHouseRemoteService thisServer = this;
            AuctionHouseRemoteService stub = (AuctionHouseRemoteService)
                    UnicastRemoteObject.exportObject((AuctionHouseRemoteService)thisServer, 0);
            System.out.println("Auction House made, now binding");
            Registry registry = LocateRegistry.createRegistry(1099);
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

    /**Find the index of item
     * @param itemID Item object to look for
     * @return index of the item in items.*/
    public int findItemByID(String itemID){
        for(int i = 0; i<itemCount;i++){
            if(itemID.equals(stages[i].getItem().getID())){
                return i;
            }
        }
        return -1;
    }

    /**Check on all current auction
     * replace auction with new ones if they are done
     * */
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
                }else{
                    storage.putBack(stages[i].getItem());
                }
                item = storage.getRandomItem();
                replaceStage(i,item);
            }
        }
    }


    /**makes a new auction thread, replace the old and run it*/
    private void replaceStage(int i, Item item){
        Auction temp = new Auction(storage.getRandomItem());
        stages[i] = temp;
        Thread t = new Thread(temp);
        t.start();
    }

    /**Notify the highest bidder of given stage that they won the item
     * @param stage the auction that ended
     * */
    private void notifyWinner(Auction stage){
        try {
            String address;
            String server;
            Bid win = stage.getMaxBid();
            win.setStatus(BidStatusMessage.WINNER);
            /**Send this to agent*/
            address = win.getAgentIP();
            server = win.getAgentServer();
            connectToAgent(address, server);
            agentService.updateBid(win);
        }catch (RemoteException e){
            e.printStackTrace();
        }
    }

    /**Remote method for agent to send a bid*/
    public void makeBid(Bid bid){
        try {
            external.put(bid);
        }catch(InterruptedException e){
            e.printStackTrace();
        }
    }

    /**take out a bid from blocking queue and try to process it
     * compare the price between old bid and new bid
     * notify bidder who succeeded and the bidder who just got outbidx
     * */
    private void processBid(){
        try {
            Auction auction;
            Bid newBid = external.take();
            String address;
            String server;
            boolean check;
            if (newBid != null) {
                int index = findItemByID(newBid.getItemID());
                if (index > -1) {
                    double price = newBid.getPriceVal();
                    auction = stages[index];
                    Bid oldBid = auction.getMaxBid();
                    address = oldBid.getAgentIP();
                    server = oldBid.getAgentServer();
                    System.out.println("agent "+ newBid.getBidderID()+" tries to bid on "+auction.getItem().getNAME()+" for $"+newBid.getBidAmount());
                    System.out.printf("Old bid amount is $"+oldBid.getBidAmount());
                    /**Check if the new bid amount is higher than current max bid*/
                    if (price > oldBid.getBidAmount()) {
                        System.out.println("new bid higher than old bid, bid succeed!");
                        check = bankService.attemptBlockFunds(newBid,oldBid,accountNumber);
                        /**Request bank to check affordable*/
                        if (check) {
                            /**Inform current bidder
                             * outbid him with new bidder
                             * reset 60 sec counter*/
                            /**If old bid is not our primary empty bid*/
                            if(address != null) {
                                oldBid.setStatus(BidStatusMessage.OUTBID);
                                connectToAgent(address, server);
                                agentService.updateBid(oldBid);
                            }
                            newBid.setStatus(BidStatusMessage.ACCEPTED);
                            auction.updateBid(newBid);
                        }
                    }
                } else {
                    newBid.setStatus(BidStatusMessage.REJECTED);
                }
                address = newBid.getAgentIP();
                server = newBid.getAgentServer();
                connectToAgent(address, server);
                agentService.updateBid(newBid);

            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**Register an account at bank with ID(Used as account ID?)*/
    private void registerAtBank(){
        try {
            Registry rmiRegistry = LocateRegistry.getRegistry(bankIP);
            bankService = (BankRemoteService) rmiRegistry.lookup(bankName);  //this is for remote machines
            accountNumber = bankService.registerAuctionHouse(ip, ID);
        } catch(IOException e){
            e.printStackTrace();
        } catch(NotBoundException ex){
            ex.printStackTrace();
        }
    }

    /**Method to be called to connect to the proxy of given agent in the parameter
     * @param agentAddress the ip address of agent
     * @param agentServer the server name of agent
     * */
    private void connectToAgent(String agentAddress,String agentServer) {
        try {
            Registry rmiRegistry = LocateRegistry.getRegistry(agentAddress);
            agentService = (AgentRemoteService) rmiRegistry.lookup(agentServer);
        }catch(Exception e){
            e.printStackTrace();
        }
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
    /**Runs this thread, process bids in blocking queue and checks on all auctions going on*/
    public synchronized void run() {
        /**First Thing to do when running this thread
         * initialize and register at bank*/
        initialize();
        registerAtBank();
        while(!Thread.interrupted()){
            try{
                if(!external.isEmpty()) {
                    processBid();
                }
                checkOnAuctions();
                if(storage.isEmpty()){
                    System.out.println("Storage Empty, Auction House Closing!");
                    Thread.currentThread().interrupt();
                    break;
                }
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
                deRegisterAtBank();
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
        if(args.length >2) {
            Storage storage = new Storage(book);
            storage.initialize();
            AuctionHouse auctionHouse = new AuctionHouse(storage);
            auctionHouse.bankIP = args[0];
            auctionHouse.portNumber = Integer.parseInt(args[1]);
            auctionHouse.ip = args[2];
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
