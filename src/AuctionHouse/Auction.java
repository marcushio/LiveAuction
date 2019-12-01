package AuctionHouse;

import Agent.Agent;
import Helper.Item;

public class Auction{
    private Item item;
    private double maxBid;
    private double winningBid = 0;
    private Agent agent;
    private int waitCount;

    public Auction(Item item){
        this.item = item;
        maxBid = item.getBASEPRICE();
        waitCount = 0;
    }

    public double getMaxBid(){
        return maxBid;
    }

    public Item getItem(){
        return item;
    }

    protected void outBid(double amount, Agent a){
        maxBid = amount;
        agent = a;
        waitCount = 0;
    }

    protected Agent getCurrentAgent(){
        return agent;
    }

    public String toString(){
        String s = "Item on Stage: "+item.getNAME();
        return s;
    }

    /**Tells the caller if the item is sold by checking if winningBid is being set
     * @return true if winningBid is being set, else return false
     * */
    protected boolean itemSold(){
        if(winningBid != 0){
            return true;
        }
        return false;
    }


}
