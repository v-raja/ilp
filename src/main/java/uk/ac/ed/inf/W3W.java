package uk.ac.ed.inf;

import com.google.gson.Gson;

/**
 * This class represent a what three words (w3w) phrase. This class is required by `gson` to parse the what three words
 * object from the JSON file retrieved from the webserver.
 * Note that the field names should match the keys of the a w3w `details.json` file retrieved from the webserver. Thus,
 * field names should not be changed without an appropriate change in the names of the keys of the `details.json` files.
 * @author Vivek Raja s1864074
 */
public class W3W {
    /**
     * The country of the coordiantes of the phrase.
     */
    public String country;
    /**
     * Represents the square of the w3w phrase.
     */
    public Square square;

    public static class Square {
        /**
         * The coordinates of the southwest of the square.
         */
        public Coords southwest;
        /**
         * The coordinates of the northeast of the square.
         */
        public Coords northeast;

    }

    /**
     * The nearest place to the coordinates.
     */
    public String nearestPlace;

    /**
     * The coordinates of the w3w phrase.
     */
    public Coords coordinates;

    /**
     * A class that represents the coordinates of the w3w phrase.
     */
    public static class Coords {

        /**
         * The longitude coordinate.
         */
        public double lng;

        /**
         * The latitude coordinate.
         */
        public double lat;
    }

    /**
     * The w3w phrase.
     */
    public String words;

    /**
     * The locale of the language.
     */
    public String language;

    /**
     * A link to the map of this w3w phrase from the w3w website.
     */
    public String map;

    /**
     * Converts a what three words phrase into LongLat coordinates.
     * @param W3W the what three words phrase
     * @return LongLat object of the what three words phrase
     */
    public static LongLat longLat(String W3W) {
        var w3wJSON = WebServerClient.instance.get("/words/" + W3W.replace(".", "/") + "/details.json");
        var words = new Gson().fromJson(w3wJSON, uk.ac.ed.inf.W3W.class);
        return words.getLongLat();
    }


    /**
     * @return the longitude and latitude coordinates in a LongLat object.
     */
    public LongLat getLongLat() {
        return new LongLat(coordinates.lng, coordinates.lat);
    }
}





