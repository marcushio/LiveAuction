package Agent;

import AuctionHouse.AuctionHouse;
import Bank.Bank;
import Helper.Bid;
import javafx.beans.property.*;

import java.io.IOException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import Helper.BankRemoteService;

public class Agent {
    private String accountNumber = "-1";
    private double liquidFunds = 0.00;
    private double currentBalance = 0;
    private double availableFunds = 0;
    private ListProperty<Bid> bidsPlaced = new SimpleListProperty<>();
    private ListProperty<AuctionHouse> availableHouses = new SimpleListProperty<>();
    private AuctionHouse selectedHouse;
    private StringProperty currentBalanceProperty = new SimpleStringProperty(String.valueOf(currentBalance));
    private StringProperty availableFundsProperty = new SimpleStringProperty(String.valueOf(availableFunds));
    private StringProperty selectedHouseProperty = new SimpleStringProperty("NONE");
    private StringProperty name = new SimpleStringProperty("Unknown Human");
    private StringProperty selectedItemProperty = new SimpleStringProperty("NONE");
    private BankRemoteService bankService;

    public Agent(String name, String liquidFunds){
        this.name.set(name);
        this.liquidFunds = Double.valueOf(liquidFunds);
        try {
            bankService = new Bank();//(BankRemoteService) Naming.lookup("192.168.86.74/BankServer");
        }
        catch(IOException e){
            System.out.println("IO Exception");
        }
//        catch(NotBoundException e){
//            System.out.println("Not bound exception");
//        }
       // accountNumber = bankService.registerAgent(name, liquidFunds);
        try{
           accountNumber = bankService.registerAgent(name,Double.valueOf(liquidFunds));
        }
        catch(RemoteException e){

        }
    }

    public StringProperty getSelectedItemProperty() {
        return selectedItemProperty;
    }

    public ListProperty<Bid> getBidsPlaced() {
        return bidsPlaced;
    }

    public ListProperty<AuctionHouse> getAvailableHouses() {
        return availableHouses;
    }

    public StringProperty getCurrentBalanceProperty() {
        return currentBalanceProperty;
    }

    public StringProperty getAvailableFundsProperty() {
        return availableFundsProperty;
    }

    public StringProperty getSelectedHouseProperty() {
        return selectedHouseProperty;
    }

    public StringProperty getName() {
        return name;
    }
}
