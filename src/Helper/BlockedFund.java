package Helper;

import Agent.Agent;

public class BlockedFund {
    double amount; //amt blocked
    int itemId;//item you bid on
    int agentId;  //agent that made the bid

    public BlockedFund(double amount, int itemId, int agentId){
        this.amount = amount;
        this.itemId = itemId;
        this.agentId = agentId;
    }

    public double getAmount(){ return amount; }

}
