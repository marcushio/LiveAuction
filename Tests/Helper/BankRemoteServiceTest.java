package Helper;

import Agent.Agent;
import AuctionHouse.AuctionHouse;
import Bank.Bank;
import org.junit.jupiter.api.Test;
import AuctionHouse.Storage;
import java.rmi.RemoteException;

import static org.junit.jupiter.api.Assertions.*;

class BankRemoteServiceTest {

    @Test
    void getActiveAuctionHouseAddresses() throws RemoteException {
        Bank bank = new Bank();
        AuctionHouseRemoteService house = new AuctionHouse(new Storage("C:/Items.txt"));
        //bank.registerAuctionHouse(house.getID());
    }


    @Test
    public void testAgentRegistration() throws RemoteException {
        String liquidFunds = "1.00";
        Agent agent = new Agent("Colton", liquidFunds);
        BankRemoteService bankService = new Bank();
        agent.register(bankService);
        System.out.println(agent.getAccountID());
        System.out.println(bankService.getBalanceString(agent.getAccountID()));
       // assertEquals("1",agent.getAccountID());
        assertEquals(liquidFunds, bankService.getAvailableFundsString(agent.getAccountID()));
    }
}