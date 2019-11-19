package Bank;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * @author: Marcus Trujillo
 * @version:
 * brief class description
 */
public class ClientHandler implements Runnable {
    private Socket client;
    private ObjectInputStream input; //consider making these bufferedWriters/bufferedReader
    private ObjectOutputStream output;

    public ClientHandler(Socket clientConnection){
        try {
            this.client = clientConnection;
            this.input = new ObjectInputStream( client.getInputStream() );
            this.output = new ObjectOutputStream( client.getOutputStream() );
            output.flush();
        } catch (IOException ex){
            System.err.println("error making connection to new client");
        }
    }

    @Override
    public void run(){
        //wait for requests and handle them
    }

    private void closeConnection(){ //right now this is only for one client fix this.
        try{
            output.close();
            input.close();
            client.close();
        } catch(IOException ex){ System.out.println("Error closing a connection"); }
    }

}
