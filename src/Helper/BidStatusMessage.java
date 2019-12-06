package Helper;

/**
 * Enum of all valid status messages with regard to a bid's outcome. These will be used by AuctionHouse
 * to tell Agent instances what the result of their bid is.
 */
public enum BidStatusMessage {
    ACCEPTED, REJECTED, OUTBID, WINNER, LOSER;
}
