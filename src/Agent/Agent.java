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
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Agent is the model used by Gui. It interacts with the servers. The Gui instance updates Agent's appropriate
 * members through bindings and the Agent instance updates its other bound variables to reflect changes resulting
 * from processing those updates. Auction houses also update the bid status of each of an agent's bids.
 */
public class Agent implements AgentRemoteService {
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

    /**
     * Make new agent instance with given name and a starting amount of funds and register it with the bank.
     * @param name String specifying the agent's name. Can include first or full name.
     * @param liquidFunds String specifying a dollar amount. Should be a float type with two decimal places
     * @parma bankAddress address of bank
     */
    public Agent(String name, String liquidFunds, String bankAddress) {
        this.name.set(name);
        this.liquidFunds = Double.parseDouble(liquidFunds);
       try {
            bankService = (BankRemoteService) Naming.lookup(bankAddress);
        }
        catch(IOException e){
            userMessages.set("IO Exception Could not connect to bank");
        }
        catch(NotBoundException e){
            userMessages.set("Not bound exception");
     }

        try{
           accountID = bankService.registerAgent(name,Double.valueOf(liquidFunds));
        }
        catch(RemoteException e){

        }
        //////////////////////IP STUFF

        InetAddress ip;
        String hostname;
        try {
            ip = InetAddress.getLocalHost();
            hostname = ip.getHostName();
            System.out.println("Your current IP address : " + ip);
            System.out.println("Your current Hostname : " + hostname);

        } catch (UnknownHostException e) {

            e.printStackTrace();
        }

        ///////////////////////////////
    }

    public void registerWithRMI() throws RemoteException{
            AgentRemoteService thisService = this;
            AgentRemoteService stub = (AgentRemoteService) UnicastRemoteObject.exportObject( (AgentRemoteService) thisService, 0);
            Registry registry = LocateRegistry.createRegistry(1099);
            registry.rebind("agentServer", stub);
            System.out.println("Server created... server running...");
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
            userMessages.set("Failed to connect to bank.");
        }
    }
    public void refreshItemList() throws RemoteException{
        /**Throw remote exception here*/
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
        try {
            currentBalanceProperty.set(bankService.getBalanceString(accountID));
            availableFundsProperty.set(bankService.getAvailableFundsString(accountID));
        } catch(RemoteException ex){
            System.err.println("couldn't refresh balance");
        }
    }

    public void connect(String selectedHouseAddress) {
        try {
            selectedHouse = (AuctionHouseRemoteService) Naming.lookup(selectedHouseAddress);
            refreshItemList();
        }
        catch(NotBoundException e){
            userMessages.set("NOT BOUND ISSUE WITH HOUSE");
        }
        catch(RemoteException e){
            userMessages.set("REMOTE HOUSE COULDN'T BE REACHED");
        }
        catch(MalformedURLException e){
            userMessages.set("BAD URL");
        }
    }

    public ObservableList<String> getHousesAddressList() {
        return auctionHouseList;
    }

    public StringProperty getCurrentBidAmount() {
        return currentBidAmount;
    }

    public void submitBid() throws RemoteException{
        Bid bid = new Bid(selectedItemProperty.get(),Double.parseDouble(currentBidAmount.get()));
        bid.setHouseAddress(selectedHouseProperty.get());
        selectedHouse.makeBid(bid);
        bidsMade.add(bid);
        refreshBidList();
    }

    /**
     * Change the BidStatusMessage of the bid. This should be called when a bid's status changes in an AuctionHouse instance
     * @param bid Bid whose status is to be changed.
     */
    public void updateBid(Bid bid) throws RemoteException{
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
            bidList.add(bid.toString());
        }
    }

    public void register(BankRemoteService bankService) throws RemoteException {
        accountID = bankService.registerAgent(getName().toString(),liquidFunds);
    }

    public String getAccountID() {
        return accountID;
    }
}
