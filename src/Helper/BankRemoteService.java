package Helper;

import AuctionHouse.AuctionHouse;

import java.net.Socket;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

public interface BankRemoteService extends java.rmi.Remote{
    //here we define what the client can call remotely
    //public boolean transferFunds(int senderId, int receiverId, double amount) throws RemoteException; transfer blocked funds took this ones place


    public String registerAgent(String name, double initialBalance) throws RemoteException; // for Agents

    //Signatures needed by AuctionHouse

    /**
     * Ask the bank if bidder have sufficient money to make this bid
     * @param accountNumber This account number of agent
     * @param amountNeeded The amount that agent should have as unblocked fund
     * @return return true if unblocked fund > amountNeeded, else return false
     * Suggestion: change account number to agent's ID, cuz AH shouldn't know their account number
     * */
    public boolean sufficientFunds(String accountNumber, double amountNeeded) throws RemoteException;


    /**
     * Register a bank account for auction house
     * @param address the port and address of this auction house.
     * @param name Actually ID is the only identification for AH
     * @return return the account number back to auction house
     * */
    public String registerAuctionHouse(String address, String name) throws RemoteException; //this is for AuctionHouse, intial balance is always 0

    /**
     * Unblock the found on item due to lost bid/out bid
     * Question: Should this be called by AuctionHouse or Agent? If AuctionHouse
     * they shouldn't know the account number of agent
     * Suggestion: Change accountNumber to Agent's ID instead, because that is the known by AH
     * @param accountNumber account number of agent
     * @param itemId unblock the fund that is blocked for this item
     * @return return true if fund unblocked, else return false
     * Suggestion: return the current unblocked fund to agent so they know their current amount?
     * */
    //public boolean unblockFunds(int accountNumber, int itemId) throws RemoteException;

    /**
     * Delete account of given account number, and transfer amount in the account
     * to the corresponding auction house/agent
     * Need to check if auction house/agent have items on bid and reject if they do
     * @param accountId Identity
     * @return true if deregister successful, else return false
     */
    public boolean deregister(String accountId) throws RemoteException;


    //Signatures For Methods That Agent requires

    /**
     * Create a new account with the given attributes and return the associated account number
     * @param name Name of the person who needs an account
     * @param initialBalance Starting balance for the account
     * @return an integer account number that the user will use to access their account
     */
    public String makeAccount(String name, Double initialBalance) throws RemoteException; //only agents should call this

    /**
     * Return a list with strings that the rmi can use to access each active auction house
     * @return List of Strings rmi can use to fetch proxy objects
     */
    public List<String> getActiveAuctionHouseAddresses() throws RemoteException;

    /**
     * Attempt to transfer funds blocked for this agent's active bids. Return true if funds were successfully transferred
     * @param accountNumber Account number of the agent's whose funds are to be transferred
     * @param itemId is the id of the item the funds were blocked for.
     * @return true if funds were transferred and false otherwise
     */
    public boolean transferBlockedFunds(String accountNumber, String itemId) throws RemoteException;

    /**
     * Return a String reflecting the total account balance of the account with the given ID
     * @param accountID ID of client requesting balance
     * @return String reflecting the total account balance of the account
     */
    String getBalanceString(String accountID) throws RemoteException;

    /**
     * Return a String reflecting the available funds of the account with the given ID
     * @param accountID ID of client requesting balance
     * @return String reflecting the difference between the account balance and total of all blocked funds for the account
     */
    String getAvailableFundsString(String accountID) throws RemoteException;

    /**
     *
     * @param newbid that the funds are being blocked for
     * @return
     * @throws RemoteException
     */
    public boolean attemptBlockFunds(Bid newbid, Bid oldBid,  String auctionHouseAccountID) throws RemoteException;

    /**
     *
     * @param bid
     * @param auctionHouseAccountID
     * @return
     * @throws RemoteException
     */
    public boolean blockFunds(Bid bid, String auctionHouseAccountID) throws RemoteException;

    /**
     * just a tester method
     * @throws RemoteException
     */
    public void remoteTest() throws RemoteException;

}
