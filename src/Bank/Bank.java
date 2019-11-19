package Bank;

import Agent.Agent;
import Helper.*;
import AuctionHouse.*;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 */
public class Bank implements RemoteService {
    private int portNumber = 12345;
    private static int currentId = 0;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private ServerSocket serverSocket;
    private Socket connection;
    private ExecutorService threadRunner = Executors.newCachedThreadPool(); //service to run connected clients
    private ConcurrentHashMap<Integer, BankAccount> clientAccounts;
    private List<AuctionHouse> auctionList = new ArrayList<AuctionHouse>();
    private ConcurrentHashMap<Integer, ClientHandler> clients; //should this hold clients or handlers?

    /**
     * Returns the balance for the account specified by the given integer
     * @param accountNumber The account number to the account for which you will return the balance
     * @return double representing the amount of money in the given account
     */
    public double getBalance(int accountNumber) {
        BankAccount account = clientAccounts.get(accountNumber);
        return account.getTotalBalance();
    }

    /**
     * transfer funds from one account to another
     * @param payerId
     * @param payeeId
     * @param amount
     * @return true if it worked
     */
    public synchronized boolean transferFunds(int payerId, int payeeId, double amount){
        //check for sufficient funds?
        boolean successful = true;
        BankAccount payer = clientAccounts.get(payerId);
        BankAccount payee = clientAccounts.get(payeeId);
        successful = payer.withdraw(amount); //if it succesfully withdrew it returns true.
        payee.deposit(amount); //probs add a way for this to fail?
        return successful;
    }

    /**
     *
     */
    public List<AuctionHouse> getAuctionHouses(){
        return auctionList;
    }

    /**
     * do all the server running
     */
    public void runServer() {
        try{
            serverSocket = new ServerSocket(portNumber, 100); //2nd param is backlog might not need it
            while(true){
                createHandler( connection = serverSocket.accept() );
            }
        } catch(IOException ex){
            System.out.println("IOException in Bank's Run Method");
        }
    }

    private void createHandler(Socket clientConnection){
        ClientHandler newHandler = new ClientHandler(clientConnection);
        clients.put(getNewId(), newHandler);
        threadRunner.execute(newHandler);
    }

    private synchronized int getNewId(){
        return ++currentId;
    }


    public static void main(String[] args){

    }
}
