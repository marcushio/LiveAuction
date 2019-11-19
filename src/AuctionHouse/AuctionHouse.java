package AuctionHouse;

import Helper.Bid;
import Helper.Item;
import java.util.HashMap;
import java.util.UUID;

/**Object Representing Auction House*/
public class AuctionHouse implements Runnable{
    /**Unique Identification of this auction house*/
    private String ID;
    /**Bookkeeping of items*/
    private HashMap<Item,Double> items;
    /**End auction if true*/
    private Storage storage;
    private boolean over = false;
    private boolean balance;

    /**Constructs auction house with a unique ID*/
    public AuctionHouse(Storage storage){
        this.storage = storage;
        ID = UUID.randomUUID().toString();
        items = new HashMap<>();
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

    }

    /**Try to make bid*/
    private void makeBid(Bid bid){
        Item i = bid.getItem();
        double price = bid.getPriceVal();
        if(price > items.get(i)){
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
}
