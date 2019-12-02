package AuctionHouse;

import Agent.Agent;
import Helper.*;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
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
    public Auction[] stages;
    private String acountNumber;
    private boolean over = false;
    private boolean balance;
    private BlockingQueue<AuctionMessage> internal = new LinkedBlockingDeque<>();

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
        Auction temp;
        for(int i = 0; i<itemCount;i++){
            temp = new Auction(storage.getRandomItem());
            stages[i] = temp;
            Thread t = new Thread(temp);
            t.start();
        }
/*        try {
            AuctionHouseRemoteService thisServer = this;
            Naming.rebind("//127.0.0.1/"+ID, thisServer);
            System.out.println("Server created... server running...");
        } catch (RemoteException ex) {
            System.err.println("Remote exception while making a Auction House");
        } catch (MalformedURLException ex) {
            System.err.println("didn't form a correct URL for the server");
        }*/
    }

    public int findStage(Item item){
        for(int i = 0; i<itemCount;i++){
            if(item.equals(stages[i])){
                return i;
            }
        }
        return -1;
    }

    /**Make new stage to replace the old ones*/
    private void addNewStage(){
        Auction temp;
        for(int i = 0; i<itemCount; i++){
            if(stages[i] == null && !storage.isEmpty()){
                temp = new Auction(storage.getRandomItem());
                stages[i] = temp;
                Thread t = new Thread(temp);
                t.start();
            }
        }
    }

    /**Remove all auction houses that is over, and make new ones to replace them*/
    private boolean removeStage(){
        Auction stage;
        boolean check = false;
        for(int i = 0; i<itemCount; i++){
            stage = stages[i];
            if(stage.isAuctionEnd()){
                /**Inform winning or/and replace item*/
                stages[i] = null;
                check = true;
            }
        }
        return check;
    }

    /**Try to make bid*/
    public BidStatusMessage makeBid(Bid bid){
        Item i = bid.getItem();
        double price = bid.getPriceVal();
        Agent bidder = null;
        int stageIndex = findStage(i);
        if(stageIndex>-1) {
            Auction stage = stages[stageIndex];
            /**Check if the new bid amount is higher than current max bid*/
            if (i.equals(stage.getItem()) && price > stage.getMaxBid()) {
                /**Request bank to check affordable*/
                if (true) {
                    Agent currentBidder = stage.getCurrentAgent();
                    /**Inform current bidder
                     * outbid him with new bidder
                     * reset 60 sec counter*/
                    stage.outBid(price, bidder);
                    stage.resetCount();
                    return BidStatusMessage.ACCEPTED;
                }
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
        //System.out.println("Auction House " + ID);
        registerAtBank();
        initialize();
        while(!Thread.interrupted()){
            try{
                if(!storage.isEmpty()){
                    removeStage();
                    addNewStage();
                }else{
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
}
