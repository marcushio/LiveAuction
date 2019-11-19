package Helper;

import AuctionHouse.AuctionHouse;

import java.util.UUID;
/**Object representing the items in Auction House*/
public class Item{
    /**Name of the item, for display purpose only*/
    private final String NAME;
    /**Unique identity of a item*/
    private final String ID;
    /**Number value used to generate base price*/
    private final int RARITY;
    /**Start price of the item*/
    private final float BASEPRICE;

    /**Constructs the item with name and rarity,
     * and generate a unique ID and base price*/
    public Item(String name, int rarity){
        NAME = name;
        ID = UUID.randomUUID().toString();
        RARITY = rarity;
        BASEPRICE = (int)((10<<RARITY)*(Math.random()*10));
    }

    public String getNAME(){
        return NAME;
    }

    public String getID(){
        return ID;
    }

    public float getBASEPRICE(){
        return BASEPRICE;
    }

    public boolean equals(Item i){
        if(ID == i.getID()){
            return true;
        }
        return false;
    }

    public int getRARITY(){
        return RARITY;
    }

    public static void main(String args[]){
        System.out.println((Math.pow(10,4)*(Math.random()*10)));
    }
}
