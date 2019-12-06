package Bank;

import Helper.BankRemoteService;
import Helper.Bid;
import Helper.BlockedFund;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 */
public class Bank implements BankRemoteService { //extends UnicastRemoteObject
    //private static final long serialVersionUID = 1L; /** this needs to be changed to a specific long **/
    private static int currentId = 0;
    private ConcurrentHashMap<String, BankAccount> clientAccounts = new ConcurrentHashMap<String, BankAccount>();//<String accountID, BankAccount>
    private List<String> agentNameList = new ArrayList<>();
    private List<String> auctionHouseAddresses = new ArrayList<>(); //

    //public Bank() throws RemoteException { }

    /**
     * Returns the balance for the account specified by the given integer
     *
     * @param accountNumber The account number to the account for which you will return the balance
     * @return double representing the amount of money in the given account
     */
    public double getBalance(int accountNumber) {
        BankAccount account = clientAccounts.get(accountNumber);
        return account.getTotalBalance();
    }

    /**
     * transfer funds from one account to another
     *
     * @param payerAccountId the accountId of the person who is transferring these blocked funds.
     * @param itemId the id of the item that the funds were blocked for
     * @return true if it worked
     */
    public synchronized boolean transferBlockedFunds(String payerAccountId, String itemId) throws RemoteException {
        //check for sufficient funds?
        boolean successful = true;
        BankAccount payerAccount = clientAccounts.get(payerAccountId);
        BlockedFund transferFund = payerAccount.removeBlockedFund(itemId);
        successful = payerAccount.withdraw(transferFund.getAmount()); //if it succesfully withdrew it returns true.

        BankAccount payeeAccount = clientAccounts.get(transferFund.getAuctionHouseAccountId());
        successful = payeeAccount.deposit(transferFund.getAmount());  //if it succesfully put the money in it returns true
        return successful;
    }

    public boolean unblockFunds(String accountID, String itemID){
        BankAccount account = clientAccounts.get(accountID);
        return account.unblockFunds(itemID);
    }

    /**
     * Return a String reflecting the total account balance of the account with the given ID
     *
     * @param accountID ID of client requesting balance
     * @return String reflecting the total account balance of the account
     */
    @Override
    public String getBalanceString(String accountID) throws RemoteException{
        double balance = clientAccounts.get(accountID).getTotalBalance();
        DecimalFormat formatter = new DecimalFormat("#.00");
        String formattedBalance = formatter.format(balance);
        return formattedBalance;
    }

    /**
     * Return a String reflecting the available funds of the account with the given ID
     *
     * @param accountID ID of client requesting balance
     * @return String reflecting the difference between the account balance and total of all blocked funds for the account
     */
    public String getAvailableFundsString(String accountID) throws RemoteException{
        double availableBalance = clientAccounts.get(accountID).getAvailableBalance();
        DecimalFormat formatter = new DecimalFormat("#.00");
        String formattedBalance = formatter.format(availableBalance);
        return formattedBalance;
    }
    /**
     * checks if this clients account has sufficient funds for a bid.
     *
     * @param accountNumber
     * @return if true if enough funds are available else false
     * @throws RemoteException
     */
    @Override
    public boolean sufficientFunds(String accountNumber, double amountNeeded) throws RemoteException {
        BankAccount account = clientAccounts.get(accountNumber);
        if (account.getAvailableBalance() >= amountNeeded) {
            return true;
        }
        return false;
    }

    /**
     * Registers an agent with the bank
     *
     * @param name
     * @param initialBalance
     * @return true if we were able to register else false
     * @throws RemoteException
     */
    @Override
    public synchronized String registerAgent(String name, double initialBalance) throws RemoteException {
        System.out.println("Registering agent: " + name);
        String newId = getNewBankAccountId();
        BankAccount newAccount = new BankAccount(newId, name, initialBalance);
        clientAccounts.put(newAccount.getAccountNumber(), newAccount);
        String accountNumber = newAccount.getAccountNumber();
        if(accountNumber == null) System.out.println("account number was returned null");
        return accountNumber;
    }

    /**
     *
     * @param newbid that the funds are being blocked for
     * @param oldbid
     * @param auctionHouseAccountID
     * @return
     * @throws RemoteException
     */
    @Override
    public synchronized boolean attemptBlockFunds(Bid newbid, Bid oldbid, String auctionHouseAccountID) throws RemoteException{
        BankAccount oldbidAccount = clientAccounts.get(oldbid.getBidderID());
        BankAccount newbidAccount = clientAccounts.get(newbid.getBidderID());
        String itemID = oldbid.getItemID();
        blockFunds(newbid, auctionHouseAccountID);
        oldbidAccount.unblockFunds(itemID);
        return true;
    }

    private boolean blockFunds(Bid bid, String auctionHouseAccountID){
        String itemID = bid.getItemID();
        double amount = bid.getBidAmount();
        String accountID = bid.getBidderID();
        BankAccount account = clientAccounts.get(accountID);
        return account.blockFunds(amount, itemID, auctionHouseAccountID );
    }



    /**
     * Create a new account with the given attributes and return the associated account number
     * @param name Name of the person who needs an account
     * @param initialBalance Starting balance for the account
     * @return an integer account number that the user will use to access their account
     */
    public String makeAccount(String name, Double initialBalance) throws RemoteException {
        return registerAgent(name, initialBalance);
    }


    /**
     * Registers an auctionhouse with bank
     *
     * @param
     * @return true if we were able to register else false
     * @throws RemoteException
     */
    @Override
    public String registerAuctionHouse(String address, String name) throws RemoteException {
        System.out.println("Registering Auction House: " + name);
        BankAccount newAccount = new BankAccount(getNewBankAccountId(), name, 0);
        clientAccounts.put(newAccount.getAccountNumber(), newAccount);
        address = address + "/" + name;
        auctionHouseAddresses.add(address);
        System.out.println("Auction House registered!");
        return newAccount.getAccountNumber();
    }

    /**
     * removes account associated with this client from the bank.
     *
     * @param accountId
     * @return true if we were able to deregister else false
     * @throws RemoteException
     */
    @Override
    public boolean deregister(String accountId) throws RemoteException {
        //remove bank account
        //remove auctionhouse from list
        //remove
        if (clientAccounts.get(accountId) == null) {
            System.out.println("There was no account with that number");
            return false;
        }
        BankAccount toBeDeleted = clientAccounts.get(accountId);
        //String name = toBeDeleted.get
        clientAccounts.remove(accountId);
        System.out.println("account: " + accountId + " deregistered");
        return true;
    }

    /**
     * @return a list of all AuctionHouses that are currently registered with the bank.
     * @throws RemoteException
     */
    @Override
    public List<String> getActiveAuctionHouseAddresses() throws RemoteException {
        return auctionHouseAddresses;
    }

    private synchronized String getNewBankAccountId() {
        return Integer.toString(++currentId);
    }

    @Override
    public void remoteTest(){
        System.out.println("Hey you called this remotely!");
    }

    public static void main(String[] args) {
        try {
            System.out.println("");
            System.out.println(InetAddress.getByName(InetAddress.getLocalHost().getHostName()).toString() );
            System.out.println("making bank...");
            BankRemoteService bankServer = new Bank();
            BankRemoteService stub = (BankRemoteService) UnicastRemoteObject.exportObject( (BankRemoteService) bankServer, 0);
            System.out.println("bank made now binding");
            Registry registry = LocateRegistry.createRegistry(1099);
            registry.rebind("bankServer", stub);
            System.out.println("Server created... server running...");

        } catch (RemoteException ex) {
            System.err.println("Remote exception while making a new bank.");
        } catch(UnknownHostException ex){
            ex.printStackTrace();
        }
    }


}
