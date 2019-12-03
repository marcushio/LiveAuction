package AuctionHouse;

import Helper.Item;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class Storage {
    private final String BOOK;
    protected ArrayList<Item> items = new ArrayList<>();

    public Storage(String book){
        BOOK = book;
    }

    public void initialize() throws IOException {
        BufferedReader maze = null;
        String line;
        int rarity = 1;
        try {
            maze = new BufferedReader(new FileReader(BOOK));
            maze.mark(1000);
            while ((line = maze.readLine()) != null) {
                if(line.length() == 1){
                    rarity = line.charAt(0)-'0';
                }else{
                    items.add(new Item(line,rarity));
                }
            }
        }finally {
            if (maze != null) {
                maze.close();
            }
        }
        Collections.shuffle(items);
    }

    /**Takes out a random item from the storage for sale in AH
     * @return random item in storage
     * */
    protected Item getRandomItem(){
        int random = (int)(Math.random()*items.size());
        Item temp = items.get(random);
        items.remove(temp);
        return temp;
    }


    public boolean isEmpty(){
        if(items.isEmpty()){
            return true;
        }else{
            return false;
        }
    }
}
