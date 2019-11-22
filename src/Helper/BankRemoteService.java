package Helper;

import AuctionHouse.AuctionHouse;

import java.net.Socket;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

public interface BankRemoteService extends java.rmi.Remote{
    //here we define what the client can call remotely
    public boolean sufficientFunds(int accountNumber, int amountNeeded) throws RemoteException;
    public int registerAgent(String name, double initialBalance) throws RemoteException; // for Agents
    public int registerAuctionHouse(String name) throws RemoteException; //this is for AuctionHouse, intial balance is always 0
    public boolean transferFunds(int senderId, int receiverId, double amount) throws RemoteException;
    public boolean unblockFunds(int accountNumber, int itemId) throws RemoteException;
    public boolean deregister(int accountNumber) throws RemoteException;

    //Signatures For Methods That Agent requires

    /**
     * Create a new account with the given attributes and return the associated account number
     * @param name Name of the person who needs an account
     * @param initialBalance Starting balance for the account
     * @return an integer account number that the user will use to access their account
     */
    public int makeAccount(String name, Double initialBalance) throws RemoteException; //only agents should call this

    /**
     * Return a list with strings that the rmi can use to access each active auction house
     * @return List of Strings rmi can use to fetch proxy objects
     */
    public List<String> getActiveAuctionHouseAddresses() throws RemoteException;

    /**
     * Attempt to transfer funds blocked for this agent's active bids. Return true if funds were successfully transferred
     * @param accountNumber Account number of the agent's whose funds are to be transferred
     * @return true if funds were transferred and false otherwise
     */
    public boolean transferBlockedFunds(int accountNumber, int itemId) throws RemoteException;

}
