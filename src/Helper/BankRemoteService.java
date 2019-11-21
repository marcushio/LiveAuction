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
    public List<String> getAuctionHouseAddresses() throws RemoteException;

}
