package Helper;

import Agent.Agent;

public class BlockedFund {
    float amount; //amt blocked
    String itemId;//item you bid on
    Agent agent;  //agent that made the bid

    public BlockedFund(float amount, String itemId, Agent agent){
        this.amount = amount;
        this.itemId = itemId;
        this.agent = agent;
    }


}
