

package uk.ac.ed.inf;
import com.mapbox.geojson.*;

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

        FeatureCollection multiLineStringFromJson = FeatureCollection.fromJson("{\"type\":\"FeatureCollection\",\"features\":[{\"type\":\"Feature\",\"geometry\":{\"type\":\"Polygon\",\"coordinates\":[[[-3.1909758,55.9452678],[-3.1909195,55.9451867],[-3.1909356,55.9451597],[-3.1909624,55.9451296],[-3.19089,55.9449944],[-3.1909543,55.9449794],[-3.190898,55.9448668],[-3.1909624,55.9448412],[-3.1909731,55.9447947],[-3.1909302,55.9447541],[-3.1908336,55.9447451],[-3.1908014,55.944673],[-3.1910053,55.9446324],[-3.1909516,55.9445063],[-3.1896856,55.944706],[-3.1898895,55.9451251],[-3.18975,55.9451431],[-3.1897151,55.9450861],[-3.1896508,55.9450981],[-3.1896347,55.9450846],[-3.1891921,55.9451882],[-3.1891868,55.9452948],[-3.1892189,55.9454105],[-3.1893745,55.9455382],[-3.1897178,55.9455126],[-3.1909758,55.9452678]]]},\"properties\":{\"name\":\"McEwan Hall Complex\",\"fill\":\"#ff0000\"}},{\"type\":\"Feature\",\"geometry\":{\"type\":\"Polygon\",\"coordinates\":[[[-3.1894711,55.9447436],[-3.1893852,55.9447586],[-3.1894147,55.9448127],[-3.189058,55.9448683],[-3.1890124,55.9448578],[-3.1889427,55.9448683],[-3.1889212,55.9448307],[-3.1890017,55.9448187],[-3.1889373,55.944691],[-3.188771,55.9447121],[-3.1887898,55.9447526],[-3.1885833,55.9447842],[-3.1885055,55.9448067],[-3.1883606,55.9448262],[-3.1882775,55.9448578],[-3.1883204,55.9449359],[-3.1883955,55.9449223],[-3.1884411,55.945017],[-3.1884223,55.945032],[-3.1884196,55.9450575],[-3.1884357,55.9450876],[-3.1884921,55.9450921],[-3.188535,55.9450605],[-3.1885591,55.9450545],[-3.1885779,55.9450846],[-3.1887388,55.9450485],[-3.1887576,55.9450906],[-3.1895757,55.9449614],[-3.1894711,55.9447436]]]},\"properties\":{\"name\":\"Teviot\",\"fill\":\"#ff0000\"}},{\"type\":\"Feature\",\"geometry\":{\"type\":\"Polygon\",\"coordinates\":[[[-3.1897084,55.9446843],[-3.1896669,55.9445979],[-3.1892391,55.9446655],[-3.1892256,55.94464],[-3.1889883,55.9446775],[-3.1890526,55.9448112],[-3.1892914,55.9447736],[-3.1892833,55.9447511],[-3.1897084,55.9446843]]]},\"properties\":{\"name\":\"Wilkie Building\",\"fill\":\"#ff0000\"}},{\"type\":\"Feature\",\"geometry\":{\"type\":\"Polygon\",\"coordinates\":[[[-3.1893155,55.9444169],[-3.1892525,55.944284],[-3.1878483,55.9444973],[-3.1879744,55.9447624],[-3.188201,55.9447263],[-3.1881326,55.9445926],[-3.1882748,55.9445701],[-3.1883392,55.9447181],[-3.1891036,55.9446002],[-3.189058,55.9445078],[-3.1889319,55.9445258],[-3.1888957,55.9444545],[-3.1890634,55.9444274],[-3.1890768,55.944456],[-3.1893155,55.9444169]]]},\"properties\":{\"name\":\"Psychology and Neuroscience\",\"fill\":\"#ff0000\"}},{\"type\":\"Feature\",\"geometry\":{\"type\":\"Polygon\",\"coordinates\":[[[-3.1906419,55.9445543],[-3.1906231,55.9445213],[-3.1907049,55.9445063],[-3.1906727,55.9444424],[-3.1910402,55.9443839],[-3.191075,55.9440151],[-3.190953,55.9440339],[-3.190949,55.9440271],[-3.1906579,55.9440707],[-3.1906633,55.9440812],[-3.1893437,55.944284],[-3.189467,55.9445408],[-3.1895623,55.9445258],[-3.189569,55.9445371],[-3.1902248,55.9444342],[-3.1903066,55.9446047],[-3.1905077,55.9445731],[-3.1904943,55.9445408],[-3.1905654,55.9445296],[-3.1905802,55.9445641],[-3.1906419,55.9445543]]]},\"properties\":{\"name\":\"Chrystal Macmillan and Hugh Robson\",\"fill\":\"#ff0000\"}},{\"type\":\"Feature\",\"geometry\":{\"type\":\"Point\",\"coordinates\":[-3.191594,55.943658]},\"properties\":{\"name\":\"Soderberg Cafe\",\"location\":\"army.monks.grapes\",\"marker-symbol\":\"landmark\",\"marker-color\":\"#0000ff\"}},{\"type\":\"Feature\",\"geometry\":{\"type\":\"Point\",\"coordinates\":[-3.186199,55.945734]},\"properties\":{\"name\":\"Beirut Restaurant\",\"location\":\"blocks.found.civic\",\"marker-symbol\":\"landmark\",\"marker-color\":\"#0000ff\"}},{\"type\":\"Feature\",\"geometry\":{\"type\":\"Point\",\"coordinates\":[-3.186874,55.944494]},\"properties\":{\"name\":\"Appleton Tower\",\"location\":\"nests.takes.print\",\"marker-symbol\":\"building\",\"marker-color\":\"#ffff00\"}},{\"type\":\"Feature\",\"geometry\":{\"type\":\"Polygon\",\"coordinates\":[[[-3.192473,55.946233],[-3.184319,55.946233],[-3.184319,55.942617],[-3.192473,55.942617],[-3.192473,55.946233]]]},\"properties\":{\"name\":\"Drone confinement zone\",\"fill\":\"none\"}},{\"type\":\"Feature\",\"geometry\":{\"type\":\"Point\",\"coordinates\":[-3.185332,55.944656]},\"properties\":{\"name\":\"Bing Tea\",\"location\":\"looks.clouds.daring\",\"marker-symbol\":\"cafe\",\"marker-color\":\"#ff0000\"}},{\"type\":\"Feature\",\"geometry\":{\"type\":\"Point\",\"coordinates\":[-3.185236,55.944709]},\"properties\":{\"name\":\"The Picnic Basket\",\"location\":\"fund.dreams.years\",\"marker-symbol\":\"cafe\",\"marker-color\":\"#ff0000\"}},{\"type\":\"Feature\",\"geometry\":{\"type\":\"Point\",\"coordinates\":[-3.186103,55.944656]},\"properties\":{\"name\":\"The Nile Valley\",\"location\":\"pest.round.peanut\",\"marker-symbol\":\"cafe\",\"marker-color\":\"#ff0000\"}},{\"type\":\"Feature\",\"geometry\":{\"type\":\"Point\",\"coordinates\":[-3.191257,55.945626]},\"properties\":{\"name\":\"Greggs\",\"location\":\"milky.hers.focus\",\"marker-symbol\":\"cafe\",\"marker-color\":\"#ff0000\"}},{\"type\":\"Feature\",\"geometry\":{\"type\":\"Point\",\"coordinates\":[-3.191065,55.945626]},\"properties\":{\"name\":\"Rudis\",\"location\":\"sketch.spill.puzzle\",\"marker-symbol\":\"cafe\",\"marker-color\":\"#ff0000\"}},{\"type\":\"Feature\",\"geometry\":{\"type\":\"Point\",\"coordinates\":[-3.188656,55.945868]},\"properties\":{\"name\":\"Bristo Sq North\",\"location\":\"surely.native.foal\",\"marker-symbol\":\"cross\",\"marker-color\":\"#00bb00\"}},{\"type\":\"Feature\",\"geometry\":{\"type\":\"Point\",\"coordinates\":[-3.188367,55.945356]},\"properties\":{\"name\":\"Bristo Sq South East\",\"location\":\"linked.pads.cigar\",\"marker-symbol\":\"cross\",\"marker-color\":\"#00bb00\"}},{\"type\":\"Feature\",\"geometry\":{\"type\":\"Point\",\"coordinates\":[-3.188512,55.944036]},\"properties\":{\"name\":\"George Sq North\",\"location\":\"eager.them.agenda\",\"marker-symbol\":\"cross\",\"marker-color\":\"#00bb00\"}},{\"type\":\"Feature\",\"geometry\":{\"type\":\"Point\",\"coordinates\":[-3.188174,55.943551]},\"properties\":{\"name\":\"George Sq Central\",\"location\":\"truck.hits.early\",\"marker-symbol\":\"cross\",\"marker-color\":\"#00bb00\"}},{\"type\":\"Feature\",\"geometry\":{\"type\":\"Point\",\"coordinates\":[-3.187837,55.943497]},\"properties\":{\"name\":\"George Sq South East\",\"location\":\"spell.stick.scale\",\"marker-symbol\":\"cross\",\"marker-color\":\"#00bb00\"}},{\"type\":\"Feature\",\"geometry\":{\"type\":\"Point\",\"coordinates\":[-3.18933,55.943389]},\"properties\":{\"name\":\"George Sq West\",\"location\":\"less.change.atomic\",\"marker-symbol\":\"cross\",\"marker-color\":\"#00bb00\"}}]}");

        ArrayList<Point> moveList = new ArrayList<>();
        for (LongLat l: movePointList) {
            Point p = Point.fromLngLat(l.longitude, l.latitude);
            moveList.add(p);
        }
        var mapFeatures = this.generateGeoJsonMarkers(addresses);
        var lineStringFeature = Feature.fromGeometry(LineString.fromLngLats(moveList));
        mapFeatures.add(lineStringFeature);
        mapFeatures.addAll(multiLineStringFromJson.features());
        this.geojsonMap = FeatureCollection.fromFeatures(mapFeatures);
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


