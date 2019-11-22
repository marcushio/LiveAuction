package Helper;

import Agent.Agent;

public class BlockedFund {
    double amount; //amt blocked
    String itemId;//item you bid on
    String agentId;  //agent that made the bid
    String auctionHouseAccountId;

    public BlockedFund(double amount, String itemId, String agentId, String auctionHouseAccountId){
        this.amount = amount;
        this.itemId = itemId;
        this.agentId = agentId;
        this.auctionHouseAccountId = auctionHouseAccountId;
    }

    public double getAmount(){ return amount; }

    public String getItemId(){ return itemId; }

    public String getAuctionHouseAccountId(){ return auctionHouseAccountId; }

}
