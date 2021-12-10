package uk.ac.ed.inf;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import java.util.*;

/**
 * This class encapsulates the logic required to access information of the menus of the sandwich shops available on a
 * webserver. The webserver that the menus are accessed from are specified in the constructor.
 * @author Vivek Raja s1864074
 */
public class Menus {

  public static Menus instance = new Menus();

  /**
   * The delivery cost in pence.
   */
  private final int DELIVERY_COST_IN_PENCE = 50;

  /**
   * A HashMap of all the sandwich shops available for the drone delivery service. The key is the name of the shop
   * and value is the `SandwichShop` object. Currently, this object is unused by methods in this class.
   */
  private Map<String, Shop> sandwichShopsMap = new HashMap<>();

  /**
   * A HashMap of all the items available for the drone delivery service. The key is the name of the item and value is
   * the `Item` object.
   */
  private Map<String, Shop.Item> itemsMap = new HashMap<>();

  private Map<Shop.Item, Shop> itemsShopMap = new HashMap<>();

  private final String menusRequestPath = "/menus/menus.json";

  /**
   * Instantiates the Menu object which retrieves and parses the menus from the specified webserver.
   * The menus are fetched and parsed on instantiation of this object. To retrieve the updated menus from the webserver,
   * another instance of this class will have to be created.
   */
  public Menus() {
    fetchMenu();
  }

  public void fetchMenu() {
    String menusJsonResponse = WebServerClient.instance.get(menusRequestPath);
    parseMenus(menusJsonResponse);
  }

  /**
   * Parses the list of sandwich shops and their items into the private fields `sandwichShopsMap` and `itemsMap`.
   * @param menusJson The list of sandwich shops in JSON format.
   */
  private void parseMenus(String menusJson) {
    // Parse the JSON list of shops into `ArrayList<SandwichShop>`
    Type sandwichShopListType = new TypeToken<ArrayList<Shop>>() {}.getType();
    ArrayList<Shop> shops = new Gson().fromJson(menusJson, sandwichShopListType);

    // add each shop to the `sandwichShopsMap` hash map
    for (Shop shop : shops) {
      shop.locationInLongLat = W3W.longLat(shop.location);
      this.sandwichShopsMap.put(shop.name, shop);

      // add all items the sandwich shops sells to the `itemsMap` hash map.
      for (Shop.Item menuItem : shop.menu) {
        this.itemsMap.put(menuItem.item, menuItem);
        this.itemsShopMap.put(menuItem, shop);
      }
    }
  }

  /**
   * Computes and returns the cost of delivering the items specified by `itemNames`. The cost returned is the cost of
   * each item plus the drone delivery cost.
   * @param itemNames the names of the items whose cost will be added to the returned delivery cost.
   * @return the cost of delivering all the items (specified by `itemNames`) with the drone delivery cost included.
   */
  public int getDeliveryCost(ArrayList<String> itemNames) {
    if (itemNames.size() == 0) {
      // If no items are to be delivered, there is no item cost or drone delivery cost.
      return 0;
    }

    // Add the drone delivery cost
    int totalDeliveryCost = this.DELIVERY_COST_IN_PENCE;

    // Add the cost of each item
    for (String itemName : itemNames) {
      Shop.Item item = this.itemsMap.get(itemName);
      totalDeliveryCost += item.pence;
    }

    return totalDeliveryCost;
  }

  public ArrayList<Shop.Item> parseItems(ArrayList<String> itemNames) {
    ArrayList<Shop.Item> items = new ArrayList<>();

    for (String itemName : itemNames) {
      Shop.Item item = this.itemsMap.get(itemName);
      items.add(item);
    }

    return items;
  }

  public ArrayList<Shop> getShopsForItems(ArrayList<Shop.Item> items) {
    Set<Shop> shops = new LinkedHashSet<>();
    for (Shop.Item item : items) {
      shops.add(this.itemsShopMap.get(item));
    }

    ArrayList<Shop> shopList = new ArrayList<Shop>();
    shopList.addAll(shops);
    return shopList;
  }

}
