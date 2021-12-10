package uk.ac.ed.inf;

import java.sql.*;
import java.sql.Date;
import java.util.*;

public class DBClient {


    public static DBClient instance = new DBClient("localhost", 1527);

    private static Connection conn;
    private static PreparedStatement selectOrder;
    private static PreparedStatement selectOrderDetails;
    private static Statement statement;

    public DBClient(String host, int port) {
        setServer(host, port);
    }

    private DBClient() {}

    public void setServer(String host, int port) {
        String databaseURL = String.format("jdbc:derby://%s:%d/derbyDB", host, port);
        this.conn = connect(databaseURL);
        this.selectOrder = setSelectOrderQuery();
        this.selectOrderDetails = setSelectOrderDetailsQuery();
        try {
            this.statement = conn.createStatement();
        } catch (SQLException e) {
            disconnect();
            e.printStackTrace();
            System.exit(1);
        }
    }

    private Connection connect(String databaseURL) {
        try {
            return DriverManager.getConnection(databaseURL);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private PreparedStatement setSelectOrderQuery() {
        try {
            return this.conn.prepareStatement("SELECT * FROM orders WHERE deliveryDate = ?");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private PreparedStatement setSelectOrderDetailsQuery() {
        try {
            return conn.prepareStatement("SELECT * FROM orderDetails WHERE orderNo = ?");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ArrayList<Order> getOrders(int year, int month, int date) {
        if (conn == null) {
            System.out.println("Conn is null");
        }

        ArrayList<Order> orders = new ArrayList<>();

        GregorianCalendar gc = new GregorianCalendar(year, month, date);
        java.sql.Date sqlDate = new java.sql.Date(gc.getTimeInMillis());
        try {
            selectOrder.setDate(1, sqlDate);

            ResultSet rs = selectOrder.executeQuery();
            while (rs.next()) {
                String orderNo = rs.getString("orderNo");
                Date deliveryDate = rs.getDate("deliveryDate");
                String customer = rs.getString("customer");
                String deliverTo = rs.getString("deliverTo");
                ArrayList<String> itemNames = getOrderDetails(orderNo);
                Order order = new Order(orderNo, deliveryDate, customer, deliverTo, itemNames);
                orders.add(order);
            }
        } catch (SQLException e) {
            disconnect();
            e.printStackTrace();
            System.exit(1);
        }
        return orders;
    }

    public ArrayList<String> getOrderDetails(String orderNo) {
        ArrayList<String> items = new ArrayList<>();
        try {
            selectOrderDetails.setString(1, orderNo);
            ResultSet rs = selectOrderDetails.executeQuery();
            while (rs.next()) {
                String item = rs.getString("item");
                items.add(item);
            }
        } catch (SQLException e) {
            disconnect();
            e.printStackTrace();
            System.exit(1);
        }

        return items;
    }

    private void disconnect() {
        try {
            selectOrder.close();
            selectOrderDetails.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private void createFlightPathTable() {
        try {
            DatabaseMetaData databaseMetadata = this.conn.getMetaData();
            String tableName = "flightpath";
            ResultSet resultSet =
                    databaseMetadata.getTables(null, null, tableName.toUpperCase(), null);

            // If the resultSet is not empty then the table exists, so we can drop it
            if (resultSet.next()) {
                statement.execute("drop table " + tableName);
            }

            statement.execute("CREATE TABLE " + tableName + " (orderNo VARCHAR(8), fromLongitude DOUBLE, fromLatitude DOUBLE, angle INT, toLongitude DOUBLE, toLatitude DOUBLE)");
        } catch (SQLException e) {
            disconnect();
            e.printStackTrace();
            System.exit(1);
        }
    }

    private void createDeliveriesTable() {
        try {
            DatabaseMetaData databaseMetadata = this.conn.getMetaData();
            String tableName = "deliveries";
            ResultSet resultSet =
                    databaseMetadata.getTables(null, null, tableName.toUpperCase(), null);

            // If the resultSet is not empty then the table exists, so we can drop it
            if (resultSet.next()) {
                statement.execute("drop table " + tableName);
            }

            statement.execute("CREATE TABLE " + tableName + " (orderNo VARCHAR(8), deliveredTo VARCHAR(19), costInPence INT)");
        } catch (SQLException e) {
            disconnect();
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void insertFlightPath(ArrayList<Move> moves) {
        createFlightPathTable();

        for (Move move : moves) {
            Order order = move.getOrder();
            String orderNo;
            if (order == null) {
                orderNo = "";
            } else {
                orderNo = order.getOrderNumber();
            }
            double fromLongitude = move.getOrig().longitude;
            double fromLatitude = move.getOrig().latitude;
            int angle = move.getAngle();
            double toLongitude = move.getDest().longitude;
            double toLatitude = move.getDest().latitude;
            try {
                statement.execute("INSERT INTO flightpath VALUES ('" + orderNo + "', " + fromLongitude + ", " + fromLatitude + ", " + angle + ", " + toLongitude + ", " + toLatitude + ")");
            } catch (SQLException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }
    }

    public void insertDeliveries(List<Order> orders) {
        createDeliveriesTable();

        for (Order order : orders) {
            String orderNo = order.getOrderNumber();
            String deliveredTo = order.getDeliverTo();
            Integer cost = order.getDeliveryCost();
            try {
                statement.execute("INSERT INTO deliveries VALUES ('" + orderNo + "', '" + deliveredTo + "', " + cost + ")");
            } catch (SQLException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }
    }

}
