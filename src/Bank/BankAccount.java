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
    private String accountId;
    private String ownerName; //necessary?
    private double availableBalance; //liquid funds that can be freely spent
    private double totalBalance; //funds that are tied up in auctions that can't be used PLUS available balance
    private ConcurrentHashMap<String, BlockedFund> blockedFunds = new ConcurrentHashMap<>();

    public BankAccount(String id, String ownerName,  double initialAmount){
        this.accountId = id;
        this.availableBalance = initialAmount;
        this.totalBalance = initialAmount;
        this.ownerName = ownerName;
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
    public String getAccountNumber(){ return accountId; }

    /**
     * get a blocked fund related to a particular item
     * @param itemId
     * @return
     */
    public BlockedFund getBlockedFund(String itemId){
        return blockedFunds.get(itemId);
    }

    public BlockedFund removeBlockedFund(String itemId){
        return blockedFunds.remove(itemId);
    }


    /**
     * Deposit a specific amount of money into an account
     * @param amount
     * @return their new total balance
     */
    public synchronized boolean deposit(double amount){
        this.availableBalance = this.availableBalance + amount;
        return true;
    }

    /**
     * Block these funds from being used because they are dedicated to an auction.
     * @param amount the amount you wish to block in this account
     * @param itemId the item for which these funds are getting set aside for.
     * @return true if we blocked the funds else false
     */
    public synchronized boolean blockFunds(double amount, String itemId, String auctionHouseAccountId){ //maybe think of returning the new available bal instead
        if(availableBalance - amount < 0){
            return false;
        }
        availableBalance -= amount;
        blockedFunds.put( itemId, new BlockedFund(amount, itemId, accountId, auctionHouseAccountId ) ) ;
        return true; //fix this currently we always return true
    }

    /**
     * Unblock funds that were set aside for a specific item
     */
    public synchronized boolean unblockFunds(String itemId){
        BlockedFund freedFunds = blockedFunds.get(itemId) ;
        availableBalance += freedFunds.getAmount();
        blockedFunds.remove(itemId);
        return true; //make a way to where this could return false or maybe throws exception.
    }
}
