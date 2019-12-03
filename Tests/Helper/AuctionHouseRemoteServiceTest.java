package Helper;
import AuctionHouse.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AuctionHouseRemoteServiceTest {

    @Test
    void getListedItems() throws RemoteException, IOException {
        Storage storage = new Storage("C:/Items.txt");
        storage.initialize();
        AuctionHouseRemoteService service = new AuctionHouse(storage);
        List<Item> items = service.getListedItems();
        System.out.println("Item List Test Complete");
    }

    @Test
    void makeBid() {
    }
}