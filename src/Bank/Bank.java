package Bank;

import AuctionHouse.AuctionHouse;
import Helper.BankRemoteService;
import Helper.Client;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 */
public class Bank extends UnicastRemoteObject implements BankRemoteService {
    private static final long serialVersionUID = 1L; /** this needs to be changed to a specific long **/
    private static int currentId = 0;

    //private ExecutorService threadRunner = Executors.newCachedThreadPool(); //service to run connected clients
    private ConcurrentHashMap<Integer, BankAccount> clientAccounts = new ConcurrentHashMap<Integer, BankAccount>();
    private List<String> agentIdList = new ArrayList<>();
    private List<String> auctionHouseList = new ArrayList<>();


    public Bank() throws RemoteException {
    }



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
     * @param payerId
     * @param payeeId
     * @param amount
     * @return true if it worked
     */
    public synchronized boolean transferFunds(int payerId, int payeeId, double amount) throws RemoteException {
        //check for sufficient funds?
        boolean successful = true;
        BankAccount payer = clientAccounts.get(payerId);
        BankAccount payee = clientAccounts.get(payeeId);
        successful = payer.withdraw(amount); //if it succesfully withdrew it returns true.
        payee.deposit(amount); //probs add a way for this to fail?
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
    public int registerAgent(String name, double initialBalance) throws RemoteException {
        agentIdList.add(name);
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
    public int registerAuctionHouse(String name) throws RemoteException {
        BankAccount newAccount = new BankAccount(getNewId(), 0);
        clientAccounts.put(newAccount.getAccountNumber(), newAccount);
        auctionHouseList.add("rmi://127.0.0.1/" + name);
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
    public List<String> getAuctionHouseAddresses() throws RemoteException {
        return new ArrayList<String>();
    }

    private synchronized int getNewId() {
        return ++currentId;
    }

    public static void main(String[] args) {
        try {
            BankRemoteService bankServer = new Bank();
            Naming.rebind("//127.0.0.1/BankServer", bankServer);
            System.out.println("Server created... server running...");
        } catch (RemoteException ex) {
            System.err.println("Remote exception while making a new bank.");
        } catch (MalformedURLException ex) {
            System.err.println("didn't form a correct URL for the server");
        }
    }


}
