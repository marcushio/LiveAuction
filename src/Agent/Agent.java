package Agent;

import AuctionHouse.AuctionHouse;
import Helper.Bid;
import javafx.beans.property.*;

import java.util.ArrayList;
import java.util.List;

public class Agent {
    private int accountNumber = -1;
    private SimpleStringProperty name = new SimpleStringProperty("Unknown Human");
    private double liquidFunds = 0.00;
    private DoubleProperty currentBalance = new SimpleDoubleProperty(0);
    private DoubleProperty availableFunds = new SimpleDoubleProperty(0);
    private ListProperty<Bid> bidsPlaced = new SimpleListProperty<>();
    private ListProperty<AuctionHouse> availableHouses = new SimpleListProperty<>();
    private AuctionHouse selectedHouse;
    private StringProperty selectedHouseString = new SimpleStringProperty("NONE");

}
