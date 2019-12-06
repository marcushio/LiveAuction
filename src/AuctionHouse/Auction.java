package AuctionHouse;

import Agent.Agent;
import Helper.Item;

public class Auction implements Runnable{
    private Item item;
    private double maxBid;
    private double winningBid = 0;
    private Agent agent;
    private int waitCount;
    private boolean auctionEnd = false;

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

    public boolean bidGoingOn(){
        if(maxBid == item.getBASEPRICE()){
            return false;
        }
        return true;
    }

    /**Out bid current current bid with a higher bid*/
    protected void outBid(double amount, Agent a){
        maxBid = amount;
        agent = a;
        waitCount = 0;
    }

    /**Get current agent*/
    protected Agent getCurrentAgent(){
        return agent;
    }

    public String toString(){
        String s = "Item on Stage: "+item.getNAME()+" $"+maxBid;
        return s;
    }

    /**Tells the auction house if auction is done
     * @return true if auction ended (after 60 sec counter)
     * */
    protected boolean isAuctionEnd(){
        return auctionEnd;
    }

    protected void resetCount(){
        waitCount = 0;
    }

    public void run(){
        System.out.println("    "+item.getNAME()+" $"+maxBid);
        while(!Thread.interrupted()){
            try{
                if(waitCount == 60){
                    /**If some one bid on the item*/
                    if(agent != null){
                        winningBid = maxBid;
                        /**Tell auction house the winning bidder/item/price*/
                    }else{
                        System.out.println("    No one bids on "+item.getNAME());
                        /**No one bid on this item, tell auction house to
                         * replace it with a new item*/
                    }
                    auctionEnd = true;
                    Thread.currentThread().interrupt();
                    break;
                }else {
                    waitCount++;
                    Thread.sleep(1000);
                }
            }catch (InterruptedException e){
                e.printStackTrace();
            }
        }
    }

}
