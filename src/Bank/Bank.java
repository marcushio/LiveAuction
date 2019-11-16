package Bank;

import Helper.*;
import AuctionHouse.*;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

/**
 *
 */
public class Bank implements Runnable {
    private int portNumber = 12345;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private ServerSocket serverSocket;
    private Socket connection;
    private ExecutorService runThreads; //service to run connected clients
    private ConcurrentHashMap<Integer, BankAccount> clientAccounts;
    private ArrayBlockingQueue<AuctionHouse> auctionList;

    /**
     * Returns the balance for the account specified by the given integer
     * @param accountNumber The account number to the account for which you will return the balance
     * @return double representing the amount of money in the given account
     */
    public double getBalance(int accountNumber) {
        BankAccount account = clientAccounts.get(accountNumber);
        return account.getTotalBalance();
    }


    @Override
    public void run() {
        try{
            serverSocket = new ServerSocket(portNumber, 100); //2nd param is backlog might not need it
            while(true){
                try {
                    waitForConnection();
                    getStreams();
                    processConnection();
                }catch(EOFException ex){
                    System.out.println("sudden connection loss to bank"); //just beg reporting for now
                } finally {
                    closeConnection();
                }
            }
        } catch(IOException ex){
            System.out.println("IOException in Bank's Run Method");
        }
    }

    private void waitForConnection() throws IOException{ //do I want this to throw an exception or do I want to catch it elsewhere
         connection = serverSocket.accept();
    }

    /**
     *
     * @throws IOException
     */
    private void getStreams() throws IOException {
        output = new ObjectOutputStream( connection.getOutputStream() );
        output.flush();
        input = new ObjectInputStream( connection.getInputStream() );
    }

    /**
     * we process connections in multiple ways
     */
    private void processConnection() {
        //if register request
        //int accountNumber = request.getAccountNumber();
        //runThreads.execute( new AccountRegistrar( accountNumber) );
        //
    }

    private void closeConnection(){
        try{
            output.close();
            input.close();
            connection.close();
        } catch(IOException ex){ System.out.println("Error closing a connection"); }
    }

    public static void main(String[] args){

    }
}
