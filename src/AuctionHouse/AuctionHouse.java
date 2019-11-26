package AuctionHouse;

import Helper.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**Object Representing Auction House*/
public class AuctionHouse implements Runnable{
    /**Unique Identification of this auction house*/
    private String ID;
    /**Bookkeeping of items*/
    private int itemCount = 4;
    private ArrayList<Item> items;
    /**End auction if true*/
    private Storage storage;
    public Auction stage;
    private String acountNumber;
    private boolean over = false;
    private boolean balance;

    /**Constructs auction house with a unique ID*/
    public AuctionHouse(Storage storage){
        this.storage = storage;
        ID = UUID.randomUUID().toString();
        items = new ArrayList<>();
    }

    /**Return a list of items in this Auction*/
    public List<Item> getListedItems() {
        return items;
    }

    /**Returns a status message to agent about how the bid went*/
    public BidStatusMessage acceptBid(Bid bid){
        return BidStatusMessage.ACCEPTED;
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
        for(int i = 0; i<itemCount-1;i++){
            items.add(storage.getRandomRegular());
        }
        items.add(storage.getRandomLegendary());
        stage = new Auction(items.get(0));
    }

    private void sortByBasePrice(){
        int highest = 0;
        Item temp;
        for(int i = 0; i<itemCount;i++){
            temp = items.get(i);
            if(temp.getBASEPRICE() > highest){

            }
        }
    }

    /**Try to make bid*/
    private void makeBid(Bid bid){
        Item i = bid.getItem();
        double price = bid.getPriceVal();
        if(i.equals(stage.getItem()) && price > stage.getMaxBid()){
            /**Request bank to check affordable
             * If so make the bid
             * else reject agent
             * */
        }
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
            if(items.isEmpty()){
                over = true;
                break;
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
        String s = "Auction House "+ID+"\nItems:\n";
        Item temp;
        for (int i = 0; i< itemCount;i++){
            temp = items.get(i);
            s += temp.getNAME() +" $"+temp.getBASEPRICE()+"\n";
        }
        return s;
    }
}
