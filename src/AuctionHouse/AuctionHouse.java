package AuctionHouse;

import Helper.Bid;
import Helper.Item;

import java.util.ArrayList;
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
    public Item stage;
    private boolean over = false;
    private boolean balance;

    /**Constructs auction house with a unique ID*/
    public AuctionHouse(Storage storage){
        this.storage = storage;
        ID = UUID.randomUUID().toString();
        items = new ArrayList<>();
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
        stage = items.get(0);
    }

    private void sortByPrice(){
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
        if(price > stage.getBASEPRICE()){
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
