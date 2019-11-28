package Bank;

import Helper.BankRemoteService;
import Helper.BlockedFund;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
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

    public Bank() throws RemoteException { }


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
        agentNameList.add(name);
        BankAccount newAccount = new BankAccount(getNewId(), initialBalance);
        clientAccounts.put(newAccount.getAccountNumber(), newAccount);
        return newAccount.getAccountNumber();
    }

    /**
     * Create a new account with the given attributes and return the associated account number
     * @param name Name of the person who needs an account
     * @param initialBalance Starting balance for the account
     * @return an integer account number that the user will use to access their account
     */
    public String makeAccount(String name, Double initialBalance) throws RemoteException {
        BankAccount newAccount = new BankAccount(getNewId(), initialBalance);
        clientAccounts.put(newAccount.getAccountNumber(), newAccount);
        return newAccount.getAccountNumber();
    }


    /**
     * Registers an auctionhouse with bank
     *
     * @param
     * @return true if we were able to register else false
     * @throws RemoteException
     */
    @Override
    public String registerAuctionHouse(String name) throws RemoteException {
        BankAccount newAccount = new BankAccount(getNewId(), 0);
        clientAccounts.put(newAccount.getAccountNumber(), newAccount);
        auctionHouseAddresses.add(name);
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
     * @param accountNumber
     * @return true if we were able to deregister else false
     * @throws RemoteException
     */
    @Override
    public boolean deregister(int accountNumber) throws RemoteException {
        /*
        if (clients.get(accountNumber) == null) {
            return false;
        }
        Client auctionHouse = clients.get(accountNumber);
        if (auctionHouse instanceof AuctionHouse) {
            auctionList.remove(auctionHouse);
        }
        clientAccounts.remove(accountNumber);
        clients.remove(accountNumber);
        return true;

         */
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

    private synchronized String getNewId() {
        return Integer.toString(++currentId);
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
        /*
        catch (MalformedURLException ex) {
            System.err.println("didn't form a correct URL for the server");
        }
         */
    }


}
