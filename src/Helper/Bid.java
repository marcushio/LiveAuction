package Helper;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * Encapsulates information about a bid which consists of an amount and and item.
 */
public class Bid {
    private Item item;
    private DoubleProperty dollarAmount= new SimpleDoubleProperty(0);

    /**
     * Constructor that sets the item and dollar amount the user will pay for it
     * @param item Item to be bid on
     * @param dollarAmount Amount to pay if the item is won
     */
    public Bid(Item item, double dollarAmount){
        this.item = item;
        this.dollarAmount.set(dollarAmount);
    }
}

