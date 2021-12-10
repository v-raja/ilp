package uk.ac.ed.inf;

import java.util.ArrayList;
import java.util.Date;

public class Order {

    private String orderNo;
    private String deliverTo;
    private ArrayList<Shop.Item> items;
    private ArrayList<Shop> shops;
    private LongLat deliverToInLongLat;
    private int deliveryCost;
    private boolean isComplete = false;

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

    public String getOrderNumber() {
        return orderNo;
    }

    public String getDeliverTo() {
        return deliverTo;
    }

    public boolean getIsComplete() {
        return isComplete;
    }

    public ArrayList<Shop> getShops() {
        return shops;
    }

    public ArrayList<Shop.Item> getItems() {
        return items;
    }

    public LongLat getDeliverToInLongLat() {
        return deliverToInLongLat;
    }
 }
