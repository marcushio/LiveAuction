package Helper;

import java.rmi.RemoteException;

/**
 * If you need to make calls on remote Agents this is the place to put the method signatures
 */
public interface AgentRemoteService extends java.rmi.Remote {
    /**
     * Change the BidStatusMessage of the bid. This should be called when a bid's status changes in an AuctionHouse instance
     * @param bid Bid whose status is to be changed.
     */
    public void updateBid(Bid bid) throws RemoteException;
}
