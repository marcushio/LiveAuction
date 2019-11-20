package Agent;

import AuctionHouse.AuctionHouse;
import Helper.Bid;
import javafx.beans.property.*;

import java.util.ArrayList;
import java.util.List;

public class Agent {
    private int accountNumber = -1;
    private double liquidFunds = 0.00;
    private double currentBalance = 0;
    private double availableFunds = 0;
    private ListProperty<Bid> bidsPlaced = new SimpleListProperty<>();
    private ListProperty<AuctionHouse> availableHouses = new SimpleListProperty<>();
    private AuctionHouse selectedHouse;
    private StringProperty currentBalanceProperty = new SimpleStringProperty(String.valueOf(currentBalance));
    private StringProperty availableFundsProperty = new SimpleStringProperty(String.valueOf(availableFunds));
    private StringProperty selectedHouseProperty = new SimpleStringProperty("NONE");
    private StringProperty name = new SimpleStringProperty("Unknown Human");
    private StringProperty selectedItemProperty = new SimpleStringProperty("NONE");

    public StringProperty getSelectedItemProperty() {
        return selectedItemProperty;
    }

    public ListProperty<Bid> getBidsPlaced() {
        return bidsPlaced;
    }

    public ListProperty<AuctionHouse> getAvailableHouses() {
        return availableHouses;
    }

    public StringProperty getCurrentBalanceProperty() {
        return currentBalanceProperty;
    }

    public StringProperty getAvailableFundsProperty() {
        return availableFundsProperty;
    }

    public StringProperty getSelectedHouseProperty() {
        return selectedHouseProperty;
    }

    public StringProperty getName() {
        return name;
    }
}
