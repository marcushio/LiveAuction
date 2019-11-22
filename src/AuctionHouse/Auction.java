package AuctionHouse;

import Helper.Item;

public class Auction {
    private Item item;
    private double maxBid;

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


}
