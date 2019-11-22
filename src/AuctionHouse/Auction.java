package AuctionHouse;

import Helper.Item;

public class Auction {
    private Item item;
    private double maxBid;
    private String biderID;

    public Auction(Item item){
        this.item = item;
        maxBid = item.getBASEPRICE();
    }

    public double getMaxBid(){
        return maxBid;
    }

    public Item getItem(){
        return item;
    }

    protected void outBid(double amount, String id){
        maxBid = amount;
        biderID = id;
    }

    public String toString(){
        String s = "Item on Stage: "+item.getNAME();
        return s;
    }

}
