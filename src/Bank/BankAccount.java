package Bank;

import Helper.*;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: Marcus Trujillo
 * @version: 11/10/2019
 *
 * an Account
 */
public class BankAccount {
    private int accountNumber;
    private int ownerId; //necessary?
    private double availableBalance; //liquid funds that can be freely spent
    private double totalBalance; //funds that are tied up in auctions that can't be used PLUS available balance
    private ConcurrentHashMap<Integer, BlockedFund> blockedFunds = new ConcurrentHashMap<>();

    public BankAccount(int id, double initialAmount){
        this.accountNumber = id;
        this.availableBalance = initialAmount;
        this.totalBalance = initialAmount;
    }

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
     * @return if we could make the withdrawal for this amount
     */
    public synchronized boolean withdraw(double amount){
        if( (this.availableBalance - amount) > 0 ) {
            this.availableBalance = this.availableBalance - amount;
            return true;
        }
        return false;
    }

    /**
     * @return the accountNumber
     */
    public int getAccountNumber(){ return accountNumber; }

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
    public synchronized boolean blockFunds(double amount, int itemId){ //maybe think of returning the new available bal instead
        availableBalance -= amount;
        blockedFunds.put( itemId, new BlockedFund(amount, itemId, ownerId) ) ;
        return true; //fix this currently we always return true
    }

    /**
     * Unblock funds that were set aside for a specific item
     */
    public boolean unblockFunds(int itemId){
        BlockedFund freedFunds = blockedFunds.get(itemId) ;
        availableBalance += freedFunds.getAmount();
        blockedFunds.remove(itemId);
        return true; //make a way to where this could return false or maybe throws exception.
    }
}
