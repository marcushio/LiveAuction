package AuctionHouse;

import Agent.Agent;
import Helper.*;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**Object Representing Auction House*/
public class AuctionHouse implements Runnable, AuctionHouseRemoteService{
    /**Unique Identification of this auction house*/
    private String ID;
    /**Number of auction stages at once*/
    private int itemCount = 3;
    /**Keeps track of how many items are sold here*/
    private int itemsSold = 0;
    /**End auction if true*/
    private Storage storage;
    public Auction stage;
    public Auction[] stages;
    private String acountNumber;
    private boolean over = false;
    private boolean balance;

    /**Constructs auction house with a unique ID*/
    public AuctionHouse(Storage storage){
        this.storage = storage;
        ID = UUID.randomUUID().toString();
        stages = new Auction[itemCount];
    }

    /**Return a list of items in this Auction*/
    public List<Item> getListedItems() {
        List<Item> items = new ArrayList<>();
        for(int i = 0; i<3; i++){
            items.add(stages[0].getItem());
        }
        return items;
    }

    protected boolean isOver(){
        return over;
    }

    /**Return the ID of this auction house*/
    protected String getID(){
        return ID;
    }

    /**Add items and stuff*/
    private void initialize(){
        Item temp;
        for(int i = 0; i<itemCount;i++){
            temp = storage.getRandomItem();
            stages[i] = new Auction(temp);
        }
        try {
            AuctionHouseRemoteService thisServer = this;
            Naming.rebind("//127.0.0.1/"+ID, thisServer);
            System.out.println("Server created... server running...");
        } catch (RemoteException ex) {
            System.err.println("Remote exception while making a Auction House");
        } catch (MalformedURLException ex) {
            System.err.println("didn't form a correct URL for the server");
        }
    }

    /**Seems useless now*/
/*    private void sortByBasePrice(){
        double highest = 0;
        int change = 0;
        Item temp;
        double tempPrice;
        while(change !=0) {
            change = 0;
            for (int i = 0; i < itemCount; i++) {
                temp = items.get(i);
                if ((tempPrice = temp.getBASEPRICE()) > highest) {
                    highest = tempPrice;
                    items.set(i, items.get(i + 1));
                    items.set(i + 1, temp);
                    change++;
                }
            }
        }
    }*/

    /**Try to make bid*/
    public BidStatusMessage makeBid(Bid bid){
        Item i = bid.getItem();
        double price = bid.getPriceVal();
        if(i.equals(stage.getItem()) && price > stage.getMaxBid()){
            /**Request bank to check affordable
             * If so make the bid, inform previous bid for outbid,
             * and inform new bid for winning
             * else reject agent
             * */
            Agent currentAgent = stage.getCurrentAgent();
            if(true) {

                stage.outBid(price,currentAgent);
            }
        }
        return BidStatusMessage.REJECTED;
    }

    /**Register an account at bank with ID(Used as account ID?)*/
    private void registerAtBank(){

    }

    /**Deregister at bank*/
    private void deRegisterAtBank(){
        /**Deregister at bank and receive money in account*/

    }

    @Override
    public void run() {
        /**First Thing register at bank*/
        registerAtBank();
        initialize();
        System.out.println(toString());
        while(!Thread.interrupted()){
            try{
                if(storage.isEmpty())
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
            s += stages[i].getItem().toString()+"\n";
        }
        return s;
    }
}
