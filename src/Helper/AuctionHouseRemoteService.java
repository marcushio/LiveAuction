package Helper;

import java.util.List;

public interface AuctionHouseRemoteService extends java.rmi.Remote {
    /**
     * Return a list of all items from the AuctionHouse
     *
     * @return a list of items from the auction house
     */
    public List<Item> getListedItems();

    /**
     * Accept a bid and return a status message based on how the bid is doing in the auction
     *
     * @param bid
     * @return StatusMessage specifying how the bid is doing
     */
    public BidStatusMessage makeBid(Bid bid);

}
