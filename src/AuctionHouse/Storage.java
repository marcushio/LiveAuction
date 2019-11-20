package AuctionHouse;

import Helper.Item;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class Storage {
    private final String BOOK;
    protected ArrayList<Item> regulars = new ArrayList<>();
    protected ArrayList<Item> legendaries = new ArrayList<>();

    public Storage(String book){
        BOOK = book;
    }

    protected void initialize() throws IOException {
        BufferedReader maze = null;
        String line;
        int rarity = 1;
        try {
            maze = new BufferedReader(new FileReader(BOOK));
            maze.mark(1000);
            while ((line = maze.readLine()) != null) {
                if(line.length() == 1){
                    rarity = line.charAt(0)-'0';
                }else if(rarity<7){
                    regulars.add(new Item(line,rarity));
                }else{
                    legendaries.add(new Item(line,rarity));
                }
            }
        }finally {
            if (maze != null) {
                maze.close();
            }
        }
        Collections.shuffle(regulars);
        Collections.shuffle(legendaries);
    }

    protected Item getRandomRegular(){
        int random = (int)(Math.random()*regulars.size());
        return regulars.get(random);
    }

    protected Item getRandomLegendary(){
        int random = (int)(Math.random()*legendaries.size());
        return legendaries.get(random);
    }

}
