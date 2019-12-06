package Helper;

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
    private double basePrice;

    /**Constructs the item with name and rarity,
     * and generate a unique ID and base price*/
    public Item(String name, int rarity){
        NAME = name;
        ID = UUID.randomUUID().toString();
        RARITY = rarity;
        basePrice = (int)(Math.pow(10,RARITY)*(int)
                ((Math.random()*10)+1)*3.1415926*(Math.random()));
    }

    /**Gets the name of item
     * @return a string of item's name
     * */
    public String getNAME(){
        return NAME;
    }

    /**Gets the ID of item
     * @return a string of item's ID
     * */
    public String getID(){
        return ID;
    }

    /**Gets the base price of item
     * @return double of item's base price
     * */
    public double getBasePrice(){
        return basePrice;
    }

    /**Reduce a base price of the item by 25%*/
    public void reduceBasePrice(){
        basePrice *= 0.75;
    }

    /**Compare the IDs of items to see if they are equal*/
    public boolean equals(Item i){
        if(ID == i.getID()){
            return true;
        }
        return false;
    }

}
