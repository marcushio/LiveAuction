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

import static Helper.BidStatusMessage.ACCEPTED;

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
    private String bankIP;
    private String bankName;
    private String IP;
    /**
     * Make new agent instance with given name and a starting amount of funds and register it with the bank.
     * @param name String specifying the agent's name. Can include first or full name.
     * @param liquidFunds String specifying a dollar amount. Should be a float type with two decimal places
     * @parma bankAddress address of bank
     */
    public Agent(String name, String liquidFunds, String myAddress, String bankAddress) {
        this.name.set(name);

        this.liquidFunds = Double.parseDouble(liquidFunds);
        try {
            connectToBank(bankAddress);
        }
        catch(Exception ex) {
            userMessages.set(ex.getMessage());
        }
        try {
            registerWithRMI();
        } catch (RemoteException e) {
            e.printStackTrace();
            System.out.println("AGENT COULD NOT BE REMOTE");
        }
    }
    public void test() throws RemoteException{
        System.out.println("WE HAVE REMOTOE CALLS");
    }
    private void connectToBank(String bankAddress) throws RemoteException, NotBoundException {
        String[] addressComponents = bankAddress.split("/" , 2);
        bankIP = addressComponents[0];
        bankName = addressComponents[1];
        Registry rmiRegistry = LocateRegistry.getRegistry(bankIP);
        bankService = (BankRemoteService) rmiRegistry.lookup(bankName);
        accountID = bankService.registerAgent(name.get(), liquidFunds);
    }
    //TODO give agents unique names
    public void registerWithRMI() throws RemoteException{
            AgentRemoteService thisService = this;
            AgentRemoteService stub = (AgentRemoteService) UnicastRemoteObject.exportObject( (AgentRemoteService) thisService, 0);
            //TODO account for multiple agents either make them use same rmi or generate diff ports
            Registry registry = LocateRegistry.createRegistry(1099);
            registry.rebind("agentServer", stub);
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
        String blah = selectedHouse.getItem();
        /**Throw remote exception here*/

        //List<Item> items = selectedHouse.getListedItems();
       // List<String> itemStrings = new ArrayList<>();
//        for(Item item : items){
//            itemStrings.add(item.toString());
//        }
       // itemList.clear();
       // itemList.addAll(itemStrings);
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

    public void connectToHouse(String selectedHouseAddress) {
        try {
            String addressComponents [] = selectedHouseAddress.split("/");
            Registry rmiRegistry = LocateRegistry.getRegistry(addressComponents[0]);
            selectedHouse = (AuctionHouseRemoteService) rmiRegistry.lookup(addressComponents[1]);
            String test = selectedHouse.getItem();
            //refreshItemList();
            System.out.println("DONE");
        }
        catch(NotBoundException e){
            String oldMessage = userMessages.get();
            userMessages.set(oldMessage+"\n"+"NOT BOUND" +"-----------------"+"\n");
        }
        catch(RemoteException e){
            userMessages.set("REMOTE HOUSE COULDN'T BE REACHED");
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
        System.out.println("JAIME REACHED THIS");
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


    public String getAccountID() {
        return accountID;
    }

    public boolean canExit() {
        for(Bid bid : bidsMade){
            if(bid.getStatus() == ACCEPTED) return false;
        }
        return true;
    }
}
