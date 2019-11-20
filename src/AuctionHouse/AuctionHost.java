package AuctionHouse;

import Helper.Item;

import java.io.IOException;

/**Main loop that keeps track of all auction houses*/
public class AuctionHost implements Runnable{
    private AuctionHouse[] auctionHouses = new AuctionHouse[3];
    private static String book = "E:/DistributedAuction/src/Items.txt";
    private static Storage storage;

    public AuctionHost(String book){
        this.book = book;
        storage = new Storage(book);
    }

    /**Make 3 auction houses*/
    private void initialize(){
        AuctionHouse a;
        for(int i = 0; i< 3; i++){
            a = new AuctionHouse(storage);
            auctionHouses[i] = a;
            Thread t = new Thread(a);
            t.start();
        }
    }

    /**Make new auction houses to replace the dead ones*/
    private void addAuctionHouse(){
        AuctionHouse temp;
        for(int i = 0; i<auctionHouses.length; i++){
            if(auctionHouses[i] == null){
                temp = new AuctionHouse(storage);
                auctionHouses[i] = temp;
                Thread t = new Thread(temp);
                t.start();
            }
        }
    }

    /**Remove all auction houses that is over, and make new ones to replace them*/
    private boolean removeAuctionHouse(){
        AuctionHouse a;
        for(int i = 0; i<auctionHouses.length; i++){
            a = auctionHouses[i];
            if(a.isOver()){
                auctionHouses[i] = null;
                return true;
            }
        }
        return false;
    }

    @Override
    public synchronized void run(){
        initialize();
        while(!Thread.interrupted()){
            if(removeAuctionHouse()){
                addAuctionHouse();
            }
        }
    }

    /**Runs the host thread*/
    public static void main(String args[]) throws IOException{
        if(args.length > 0){
            book = args[0];
        }
        AuctionHost host = new AuctionHost(book);
        host.storage.initialize();
        Item temp;
        for(int i = 0; i<storage.regulars.size(); i++){
            temp = storage.regulars.get(i);
            System.out.println(temp.getNAME()+" $"+temp.getBASEPRICE());
        }
        //Thread t = new Thread(host);
        //t.start();
    }
}
