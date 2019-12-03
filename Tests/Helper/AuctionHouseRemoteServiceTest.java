package Helper;
import AuctionHouse.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AuctionHouseRemoteServiceTest {

    @Test
    void getListedItems() {
        AuctionHouseRemoteService service = new AuctionHouse(new Storage("C:/Items.txt"));
        List<Item> items = service.getListedItems();
        System.out.println("Item List Test Complete");
    }

    @Test
    void makeBid() {
    }
}