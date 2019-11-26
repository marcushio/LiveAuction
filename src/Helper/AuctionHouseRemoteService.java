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
    public BidStatusMessage acceptBid(Bid bid);

    /**
     * Check the status of the given bid, set it in the given bid to the new status, and return the updated bid.
     * @param bid original bid sent when bid was placed
     * @return Same Bid instance after setStatus has been used to update it
     */
    public Bid setBidStatus(Bid bid);
}
