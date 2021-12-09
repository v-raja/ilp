package uk.ac.ed.inf;

import java.util.ArrayList;

/**
 * This class represent a sandwich shop. This class is required by `gson` to parse the menus of the sandwich shops from
 * the JSON file retrieved from the webserver.
 * Note that the field names should match the keys of the `menus.json` file retrieved from the webserver. Thus, field
 * names should not be changed without an appropriate change in the names of the keys of the `menus.json` file.
 * @see Menus
 * @author Vivek Raja s1864074
 */
public class Shop {

    /**
     * The name of the sandwich shop.
     */
    String name;

    /**
     * The location of the sandwich shop.
     */
    String location;


    LongLat locationInLongLat;

    /**
     * The menu of the sandwich shop.
     */
    ArrayList<Item> menu;


    /**
     * This class represent an item available in the menu of a sandwich shop.
     */
    public static class Item {
        /**
         * The name of the menu item.
         */
        String item;

        /**
         * The cost of the item in pence.
         */
        int pence;
    }
}
