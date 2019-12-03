package Helper;

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
}