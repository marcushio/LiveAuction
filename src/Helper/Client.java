package Helper;

import java.io.Serializable;

public interface Client extends Serializable { // both agents and auctionHouses will implement this.
    public int register(); //bank will return it's new account number
    public int getAccountNumber();
}
