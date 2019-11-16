package Bank;

import Helper.*;

import java.util.List;

/**
 * @author: Marcus Trujillo
 * @version: 11/10/2019
 *
 * an Account
 */
public class BankAccount {
    private int id;
    private int ownerId; //necessary?
    private double availableBalance; //liquid funds that can be freely spent
    private double totalBalance; //funds that are tied up in auctions that can't be used.
    private List<BlockedFund> blockedFunds;

    /**
     * @return the available balance of this account
     */
    public synchronized double getAvailableBalance(){
        return availableBalance;
    }

    /**
     * @return the total balance (available balance plus all funds that are tied up in auctions)
     */
    public synchronized double getTotalBalance(){
        return totalBalance;
    }

    /**
     * Make a withdrawal from the account
     * @param amount that you want to subtract from the amount
     * @return the new balance of the account
     */
    public synchronized double withdraw(double amount){ //consider just making this a boolean
        if( (this.availableBalance - amount) > 0 ) {
            this.availableBalance = this.availableBalance - amount;
        }
        return availableBalance;
    }

    /**
     * Deposit a specific amount of money into an account
     * @param amount
     * @return their new total balance
     */
    public synchronized double deposit(double amount){
        this.availableBalance = this.availableBalance + amount;
        return totalBalance;
    }

    /**
     * Block these funds from being used because they are dedicated to an auction.
     * @param amount the amount you wish to block in this account
     * @param itemId the item for which these funds are getting set aside for.
     * @return true if we blocked the funds else false
     */
    public synchronized boolean blockFunds(double amount, String itemId){ //maybe think of returning the new available bal instead
        availableBalance -= amount;
        return blockedFunds.add( new BlockedFund(amount, itemId, ownerId) ) ;
    }

}
