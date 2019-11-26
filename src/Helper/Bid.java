package Helper;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import java.io.Serializable;

/**
 * Encapsulates information about a bid which consists of an amount and and item.
 */
public class Bid implements Serializable {
    private String itemID;
    private double dollarAmount= 0.00;
    private String houseAddress = "";
    private BidStatusMessage status = BidStatusMessage.REJECTED;
    private StringProperty displayString = new SimpleStringProperty("");
    @Override
    public boolean equals(Object o){
        if (!(o instanceof Bid)) return false;
        Bid otherBid = (Bid) o;
        if (!otherBid.itemID.equals(this.itemID)) return false;
        if (otherBid.dollarAmount != this.dollarAmount) return false;
        return true;
    }
    @Override
    /**
     * {@inheritDoc}
     */
    public int hashCode(){
        return itemID.hashCode();
    }
    /**
     * Constructor that sets the item and dollar amount the user will pay for it
     * @param itemID ID of item to be bid on
     * @param dollarAmount Amount to pay if the item is won
     */
    public Bid(String itemID, double dollarAmount){
        this.itemID = itemID;
        this.dollarAmount = dollarAmount;
    }

    /**
     * Return the address of the house this bid is to placed in as a String
     * @return String address of the house this bid is to be placed in.
     */
    public String getHouseAddress(){
        return houseAddress;
    }
    @Override
    public String toString(){
        StringBuilder builder = new StringBuilder();
        builder.append(itemID);
        builder.append(dollarAmount);
        builder.append(status.toString());
        return builder.toString();
    }
    /**get Item String ID*/
    public String getItemID(){
        return itemID;
    }
    public Item getItem(){
        return new Item("FAKE", 10);
    }

    public double getBidAmount(){
        return dollarAmount;
    }

    /**get value of price*/
    public double getPriceVal(){
        return dollarAmount;
    }

    /**
     * Set the status of this bid. AuctionHouse should update this and send it back.
     * @param status New and updated status of this bid.
     */
    public void setStatus(BidStatusMessage status){
        this.status = status;
    }

    /**
     * Set the address to be used to connect to the house where this bid will be placed
     * @param houseAddress of the house this bid is to be placed
     */
    public void setHouseAddress(String houseAddress) {
        this.houseAddress = houseAddress;
    }
}

