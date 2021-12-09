

package uk.ac.ed.inf;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;

import java.awt.geom.Point2D;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;


public class GeoJsonMap {
     /**
     * The GeoJSON representation of the map with all features.
     */
    private FeatureCollection geojsonMap;

    /**
     * Instantiates a new GeoJSON helper.
     */
    /**
     * Writes the created GeoJSON map to a given filename.
     *
     * @param fileName the file name
     */
    public void writeToFile(String fileName) {
        if (this.geojsonMap == null) {
            System.err.println("The GeoJSON map has not been created yet.");
            System.exit(1);
        }
        try (FileWriter file = new FileWriter(fileName)) {
            file.write(this.geojsonMap.toJson());
            file.flush();
        } catch (IOException e) {
            System.err.println("There was an error in writing the GeoJSON file to the given filename: " + fileName);
            System.exit(1);
        }
    }

    /**
     * Create a GeoJSON map of type {@link FeatureCollection} and store it in the geojsonMap field.
     *
     * @param addresses the list of visited and unvisited addresses by the drone
     * @param movePointList the list of points in which drone moved (in order)
     */
    public FeatureCollection createGeoJsonMap(
            ArrayList<LongLat> addresses, ArrayList<LongLat> movePointList) {
        ArrayList<Point> moveList = new ArrayList<>();
        for (LongLat l: movePointList) {
            Point p = Point.fromLngLat(l.longitude, l.latitude);
            moveList.add(p);
        }
        var mapFeatures = this.generateGeoJsonMarkers(addresses);
        var lineStringFeature = Feature.fromGeometry(LineString.fromLngLats(moveList));
        mapFeatures.add(lineStringFeature);
        this.geojsonMap =FeatureCollection.fromFeatures(mapFeatures);
        return this.geojsonMap;
    }

    /**
     * Generates a list GeoJSON markers from a list of addresses.
     *
     * @param addresses the list of visited and unvisited addresses by the drone
     * @return the array list of GeoJSON features
     */
    private ArrayList<Feature> generateGeoJsonMarkers(ArrayList<LongLat> addresses) {
        var mapFeatures = new ArrayList<Feature>();
        for (var address : addresses) {
            mapFeatures.add(Feature.fromGeometry(Point.fromLngLat(address.longitude, address.latitude)));
        }
        return mapFeatures;
    }
}


