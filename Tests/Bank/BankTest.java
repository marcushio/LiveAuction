package Bank;

import org.junit.jupiter.api.Test;

import java.text.DecimalFormat;

import static org.junit.jupiter.api.Assertions.*;

class BankTest {

    @Test
    void getBalance() {
    }

    @Test
    void getAuctionHouseAddresses(){

    }

    @Test
    void testFormat(){
        DecimalFormat formatter = new DecimalFormat("#.00");
        String test = formatter.format(4.467);
        System.out.println(test);
    }
}