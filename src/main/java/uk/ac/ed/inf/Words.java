package uk.ac.ed.inf;

import com.google.gson.Gson;

public class Words {
    /**
     * Country.
     */
    public String country;
    /**
     * Square.
     */
    public Square square;

    public static class Square {
        /**
         * Southwest.
         */
        public Coords southwest;
        /**
         * Northeast.
         */
        public Coords northeast;

        @Override
        public String toString() {
            return "Square [southwest=" + southwest + ", northeast=" + northeast + "]";
        }
    }
    /**
     * Nearest place.
     */
    public String nearestPlace;
    /**
     * Coordinates.
     */
    public Coords coordinates;
    /**
     * type Coordinates.
     */
    public static class Coords {
        /**
         * Longitude.
         */
        public double lng;
        /**
         * Latitude.
         */
        public double lat;

        @Override
        public String toString() {
            return "Coords [lng=" + lng + ", lat=" + lat + "]";
        }
    }
    /**
     * Words.
     */
    public String words;
    /**
     * Language.
     */
    public String language;
    /**
     * Map.
     */
    public String map;

    @Override
    public String toString() {
        return "Words [country="
                + country
                + ", square="
                + square
                + ", nearestPlace="
                + nearestPlace
                + ", coordinates="
                + coordinates
                + ", words="
                + words
                + ", language="
                + language
                + ", map="
                + map
                + "]";
    }

    public static LongLat longLat(String W3W) {
        var w3wJSON = WebServerClient.instance.get("/words/" + W3W.replace(".", "/") + "/details.json");
        var words = new Gson().fromJson(w3wJSON, Words.class);
        return words.getLongLat();
    }
    public LongLat getLongLat() {
        return new LongLat(coordinates.lng, coordinates.lat);
    }
}





