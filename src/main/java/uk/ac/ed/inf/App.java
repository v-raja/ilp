package uk.ac.ed.inf;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class App {

    public static void main(String[] args) {
        WebServerClient.instance.setServer("localhost", 80);
        Menus.instance.fetchMenu();
        DBClient.instance.setServer("localhost", 1527);
        processOrdersForDate(2022, 12, 01);
        return;
//        final int DATE, MONTH, YEAR, WEBSERVER_PORT, DB_PORT;
//
//
//        try {
//            DATE = Integer.parseInt(args[0]);
//            MONTH = Integer.parseInt(args[1]);
//            YEAR = Integer.parseInt(args[2]);
//            WEBSERVER_PORT = Integer.parseInt(args[3]);
//            DB_PORT = Integer.parseInt(args[4]);
//
//            WebServerClient.instance.setServer("localhost", WEBSERVER_PORT);
//            Menus.instance.fetchMenu();
//            DBClient.instance.setServer("localhost", DB_PORT);
//            processOrdersForDate(YEAR, MONTH, DATE);
//        } catch (ArrayIndexOutOfBoundsException e) {
//            System.out.println("Not enough arguments. Please specify date, month, year of orders to process, and " +
//                    "webserver and database ports.");
//            System.exit(1);
//        }

    }

    private static void processOrdersForDate(Integer year, Integer month, Integer date) {
        // get orders
        ArrayList<Order> orders = DBClient.instance.getOrders(year, month, date);

        LongLat appletonTower = new LongLat(LongLat.APPLETON_TOWER_LONGITUDE, LongLat.APPLETON_TOWER_LATITUDE);
        // process orders and get results
        Drone drone = new Drone(appletonTower, orders);

        // output to flightpath db
        ArrayList<Move> flightPath = drone.getMoves();
        DBClient.instance.insertFlightPath(flightPath);
        DBClient.instance.insertDeliveries(drone.getOrdersDelivered());

        ArrayList<LongLat> deliveryPath = new ArrayList<>();
        ArrayList<LongLat> deliveryStops = new ArrayList<>();
        deliveryPath.add(flightPath.get(0).getOrig());
        for (Move move : flightPath) {
            if (move.getAngle() == Drone.SPECIAL_HOVERING_ANGLE) {
                deliveryStops.add(move.getDest());
            } else {
                deliveryPath.add(move.getDest());
            }
        }

        // generate geojson file
        GeoJsonMap mm = new GeoJsonMap();
        System.out.println(mm.createGeoJsonMap(deliveryStops, deliveryPath).toJson());
    }
}
