package Agent;

import AuctionHouse.AuctionHouse;
import Bank.Bank;
import Helper.*;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Agent is the model used by Gui. It interacts with the servers. The Gui instance updates Agent's appropriate
 * members through bindings and the Agent instance updates its other bound variables to reflect changes resulting
 * from processing those updates. Agent also listens for updates from the various
 */
public class Agent {
    private Set<Bid> bidsMade = new HashSet<>();
    private StringProperty userMessages = new SimpleStringProperty("");
    private StringProperty currentBidAmount = new SimpleStringProperty("0.00");
    private ObservableList<String> itemList = FXCollections.observableArrayList();
    private ObservableList<String> auctionHouseList = FXCollections.observableArrayList();
    private ObservableList<String> bidList = FXCollections.observableArrayList();
    private String accountID = "INVALID";
    private String ipAddress = "";
    private double liquidFunds = 0.00;
    private ListProperty<AuctionHouse> availableHouses = new SimpleListProperty<>();
    private AuctionHouseRemoteService selectedHouse;
    private StringProperty currentBalanceProperty = new SimpleStringProperty("");
    private StringProperty availableFundsProperty = new SimpleStringProperty("");
    private StringProperty selectedHouseProperty = new SimpleStringProperty("NONE");
    private StringProperty name = new SimpleStringProperty("Unknown Human");
    private StringProperty selectedItemProperty = new SimpleStringProperty("NONE");
    private BankRemoteService bankService;

    public Agent(String name, String liquidFunds){
        this.name.set(name);
        this.liquidFunds = Double.parseDouble(liquidFunds);
        try {
            bankService = new Bank();//(BankRemoteService) Naming.lookup("192.168.86.74/BankServer");
        }
        catch(IOException e){
            System.out.println("IO Exception From Bank Service");
        }
//        catch(NotBoundException e){
//            System.out.println("Not bound exception");
//        }
       // accountNumber = bankService.registerAgent(name, liquidFunds);
        try{
           accountID = bankService.registerAgent(name,Double.valueOf(liquidFunds));
        }
        catch(RemoteException e){

        }
    }

    public ObservableList<String> getItemStringList(){
        return itemList;
    }
    public StringProperty getSelectedItemProperty() {
        return selectedItemProperty;
    }

    public void refreshAvailableHouses() {
        try {
            List<String> houseAddresses =  bankService.getActiveAuctionHouseAddresses();
            auctionHouseList.clear();
            auctionHouseList.addAll(houseAddresses);
        }
        catch (RemoteException e){
            System.out.println("Failed to connect to bank.");
        }
    }
    public void refreshItemList(){
        List<Item> items = selectedHouse.getListedItems();
        List<String> itemStrings = new ArrayList<>();
        for(Item item : items){
            itemStrings.add(item.toString());
        }
        itemList.clear();
        itemList.addAll(itemStrings);
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

    public void refreshBalances() {
        currentBalanceProperty.set(bankService.getBalanceString(accountID));
        availableFundsProperty.set(bankService.getAvailableFundsString(accountID));
    }

    public void connect(String selectedHouseAddress) {
        try {
            selectedHouse = (AuctionHouseRemoteService) Naming.lookup(selectedHouseAddress);
        }
        catch(NotBoundException e){
            System.out.println("NOT BOUND ISSUE WITH HOUSE");
        }
        catch(RemoteException e){
            System.out.println("REMOTE HOUSE COULDN'T BE REACHED");
        }
        catch(MalformedURLException e){
            System.out.println("BAD URL");
        }
    }

    public ObservableList<String> getHousesAddressList() {
        return auctionHouseList;
    }

    public StringProperty getCurrentBidAmount() {
        return currentBidAmount;
    }

    public void submitBid() {
        Bid bid = new Bid(selectedItemProperty.get(),Double.parseDouble(currentBidAmount.get()));
        bid.setHouseAddress(selectedHouseProperty.get());
        BidStatusMessage status = selectedHouse.makeBid(bid);
        if(status == BidStatusMessage.REJECTED) userMessages.set("Bid was rejected");
        else {
            bidsMade.add(bid);
            refreshBidList();
        }
    }

    /**
     * Change the BidStatusMessage of the bid. This should be called when a bid's status changes in an AuctionHouse instance
     * @param bid Bid whose status is to be changed.
     */
    public void updateBid(Bid bid){
        bidsMade.add(bid);
        refreshBidList();
    }
    public StringProperty getMessagesProperty() {
        return userMessages;
    }

    public ObservableList<String> getBidList() {
        return bidList;
    }

    public void refreshBidList() {
        bidList.clear();
        for(Bid bid: bidsMade){
            connect(bid.getHouseAddress());
            selectedHouse.setBidStatus(bid);
            bidList.add(bid.toString());
        }
    }
}
