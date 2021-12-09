package uk.ac.ed.inf;

import java.util.ArrayList;
import java.util.Date;

public class Order {

    public String orderNo;
    public String deliverTo;
    public ArrayList<Shop.Item> items;
    public ArrayList<Shop> shops;
    public LongLat deliverToInLongLat;
    private int deliveryCost;
    public boolean isComplete = false;

    public Order(String orderNo, Date deliveryDate, String customer, String deliverTo, ArrayList<String> itemNames) {
        this.orderNo = orderNo;
        this.deliverTo = deliverTo;
        this.items = Menus.instance.parseItems(itemNames);
        this.deliveryCost = Menus.instance.getDeliveryCost(itemNames);
        this.shops = Menus.instance.getShopsForItems(items);
        this.deliverToInLongLat = Words.longLat(deliverTo);

        if (this.shops.size() > 2) {
            System.out.println("Unexpectedly got an order with more than two shops.");
        }

        if (this.items.size() > 4) {
            System.out.println("Unexpectedly got an order with more than four items.");
        }
    }

    public void markCompleted() {
        isComplete = true;
    }

    public int getDeliveryCost() {
        return deliveryCost;
    }
 }
