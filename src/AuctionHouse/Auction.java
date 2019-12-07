package AuctionHouse;
import Helper.Bid;
import Helper.Item;

import java.util.concurrent.BlockingQueue;

public class Auction implements Runnable{
    /**Keeps track of item*/
    private Item item;
    /**Keep track of highest bid*/
    private Bid maxBid;
    /**Keeps track of the max bid amount*/
    private double maxBidAmount;
    /**Keeps track of the most recent bidder*/
    /**Times the bid*/
    private int waitCount;
    /**Variable to represent the auction status
     * 0 - auction going
     * 1 - item sold
     * -1 - item recalled because of no bidder*/
    private int auctionStatus;

    public Auction(Item item){
        this.item = item;
        this.maxBid = new Bid(item.getID(),item.getBasePrice());
        maxBidAmount = item.getBasePrice();
        waitCount = 0;
        auctionStatus = 0;
    }

    protected void replaceItem(Item item){
        this.item = item;
        maxBidAmount = item.getBasePrice();
        waitCount = 0;
        auctionStatus = 0;
    }

    public double getMaxBidAmount(){
        return maxBidAmount;
    }

    public Bid getMaxBid(){
        return maxBid;
    }

    public Item getItem(){
        return item;
    }

    public boolean bidGoingOn(){
        if(maxBidAmount == item.getBasePrice()){
            return false;
        }
        return true;
    }

    /**Out bid current current bid with a higher bid*/
    protected void outBid(Bid bid){
        maxBidAmount = bid.getBidAmount();
        maxBid = bid;
        waitCount = 0;
    }

    public void updateBid(Bid bid){
        maxBid = bid;
        maxBidAmount = bid.getBidAmount();
        item.updateMax(maxBidAmount);
    }

    public String toString(){
        String s = "Item on Stage: "+item.getNAME()+" $"+maxBidAmount;
        return s;
    }

    /**Tells the auction house the auction status
     * @return integer representing the status
     * */
    protected int getAuctionStatus(){
        return auctionStatus;
    }

    /**Called when some one bids to reset count down*/
    protected void resetCount(){
        waitCount = 0;
    }

    /**Runs this thread*/
    public synchronized void run(){
        //System.out.println("    "+item.getNAME()+" $"+maxBidAmount);
        while(!Thread.interrupted()){
            try{
                if(waitCount == 30){
                    /**If some one bid on the item*/
                    if(maxBid.getAgentIP() != null){
                        auctionStatus = 1;
                        /**Tell auction house house winning bidder/item/price*/
                    }else{
                        auctionStatus = -1;
                        //System.out.println("    No one bids on "+item.getNAME());
                        /**No one bid on this item, tell auction house to
                         * replace it with a new item*/
                    }
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
