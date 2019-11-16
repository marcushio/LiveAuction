package Helper;

public enum RequestType {
    //Bank processes the below requests
    MAKE_ACCOUNT,               //return id for agent
    GET_AUCTION_HOUSE_LIST,     //return all active auction houses
    REGISTER_HOUSE,             //return id for house
    DEREGISTER_HOUSE,           //true or false depending on if de-registration was successful or not
    GET_HOST_AND_PORT,          //return an InetAddress of the specified AuctionHouse
    TRANSFER_BLOCKED_FUNDS,     //return double of new balance for account
    CHECK_SUFFICIENT_FUNDS,     //return true or false based on if the agent specified can pay for the item

    //AuctionHouse processes the below requests
    GET_LISTED_ITEMS,           //return a list of items
    PLACE_BID,                  //return true if bid was valid and false otherwise
    CHECK_BID_STATUS            //return StatusMessage
    //Agent processes the below requests

}
