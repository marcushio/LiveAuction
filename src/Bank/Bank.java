package Bank;

import Helper.BankRemoteService;
import Helper.BlockedFund;

import java.net.MalformedURLException;
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
    //private ExecutorService threadRunner = Executors.newCachedThreadPool(); //service to run connected clients
    private ConcurrentHashMap<String, BankAccount> clientAccounts = new ConcurrentHashMap<String, BankAccount>();
    private List<String> agentNameList = new ArrayList<>();
    private List<String> auctionHouseAddresses = new ArrayList<>();

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
    public boolean sufficientFunds(int accountNumber, int amountNeeded) throws RemoteException {
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
    public String registerAgent(String name, double initialBalance) throws RemoteException {
        System.out.println("Hey we're registering an agent!");
        String newId = getNewBankAccountId();
        BankAccount newAccount = new BankAccount(newId, name, initialBalance);
        clientAccounts.put(newAccount.getAccountNumber(), newAccount);
        String accountNumber = newAccount.getAccountNumber();
        if(accountNumber == null) System.out.println("account number was returned null");
        return accountNumber;
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
        auctionHouseAddresses.add(address);
        System.out.println("Auction House registered!");
        return newAccount.getAccountNumber();
    }

    /**
     * unblock the funds that were set aside for a particular auction item.
     *
     * @return true if the funds were unblocked, else false
     * @throws RemoteException
     */
    @Override
    public boolean unblockFunds(int accountNumber, int itemId) throws RemoteException {
        return clientAccounts.get(accountNumber).unblockFunds(itemId);
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
            System.out.println("making bank...");
            BankRemoteService bankServer = new Bank();
            BankRemoteService stub = (BankRemoteService) UnicastRemoteObject.exportObject( (BankRemoteService) bankServer, 0);
            System.out.println("bank made now binding");
            Registry registry = LocateRegistry.createRegistry(1099);
            registry.rebind("bankServer", stub);
            System.out.println("Server created... server running...");

        } catch (RemoteException ex) {
            System.err.println("Remote exception while making a new bank.");
        }
    }


}
