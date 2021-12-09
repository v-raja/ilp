package uk.ac.ed.inf;


import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Polygon;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * A class that encapsulates the logic of the geographic coordinate (in terms of longitude and latitude) of an object.
 * Note that latitude and longitude coordinates are treated as though they are points on a plane, not points on the
 * surface of a sphere.
 * @author Vivek Raja s1864074
 */
public class LongLat {

  /**
   * The distance tolerance (in degrees) at which two coordinates are considered to be "close to" each other.
   */
  private static final double CLOSE_TO_DISTANCE_TOLERANCE_IN_DEGREES = 0.00015;

  /**
   * The longitude coordinate of this LongLat object.
   */
  public double longitude;

  /**
   * The latitude coordinate of this LongLat object.
   */
  public double latitude;

  /**
   * The West longitude coordinate (in degrees) of the confinement area.
   */
  private static final double CONFINEMENT_AREA_BOUND_WEST_LONGITUDE = -3.192473;

  /**
   * The East longitude coordinate (in degrees) of the confinement area.
   */
  private static final double CONFINEMENT_AREA_BOUND_EAST_LONGITUDE = -3.184319;

  /**
   * The South latitude coordinate (in degrees) of the confinement area.
   */
  private static final double CONFINEMENT_AREA_BOUND_SOUTH_LATITUDE = 55.942617;

  /**
   * The North latitude coordinate (in degrees) of the confinement area.
   */
  private static final double CONFINEMENT_AREA_BOUND_NORTH_LATITUDE = 55.946233;

  /**
   * The longitude from where the drone is launched from at AT.
   */
  public static final double APPLETON_TOWER_LONGITUDE = -3.186874;

  /**
   * The latitude from where the drone is launched from at AT.
   */
  public static final double APPLETON_TOWER_LATITUDE = 55.944494;

  public static final List<Line2D> NO_FLY_ZONES_SIDES = getNoFlyZonesSides();

  private static List<Line2D> getNoFlyZonesSides() {
    var responseBody = WebServerClient.instance.get("/buildings/no-fly-zones.geojson");
    List<Feature> features = FeatureCollection.fromJson(responseBody).features();
    ArrayList<Line2D> sides = new ArrayList<>();
    for (Feature feature : features) {
      sides.addAll(getPolygonSides(feature));
    }
    return sides;
  }

  private static ArrayList<Line2D> getPolygonSides(Feature feature) {
    ArrayList<Line2D> sides = new ArrayList<>();
    var polygon = (Polygon) feature.geometry();
    var points = Objects.requireNonNull(polygon).coordinates().get(0).stream()
            .map(pt -> new Point2D.Double(pt.longitude(), pt.latitude())).collect(Collectors.toList());
    for (int i = 0; i < points.size() - 1; i++) {
      sides.add(new Line2D.Double(points.get(i), points.get(i + 1)));
    }
    return sides;
  }

  /**
   * Instantiates a LongLat object and sets the public fields `longitude` and `latitude`.
   * @param longitude The longitude of the geographic coordinate.
   * @param latitude The latitude of the geographic coordinate.
   */
  public LongLat(double longitude, double latitude) {
    setCoordinates(longitude, latitude);
  }

  /**
   * Sets the longitude and latitude fields of this LongLat object.
   * @param lng The longitude of the geographic coordinate.
   * @param lat The latitude of the geographic coordinate.
   */
  private void setCoordinates(double lng, double lat) {
    this.longitude = lng;
    this.latitude = lat;
  }

  /**
   * Returns the Euclidean distance to the LongLat object `other`. Note that latitude and longitude coordinates
   * are treated as though they are points on a plane, not points on the surface of a sphere.
   * @param other The LongLat object to measure the distance to.
   * @return The Euclidean distance in degrees to `other`.
   */
  public double distanceTo(LongLat other) {
    double lngDiffSquared = Math.pow(this.longitude - other.longitude, 2);
    double latDiffSquared = Math.pow(this.latitude - other.latitude, 2);
    return Math.sqrt(lngDiffSquared + latDiffSquared);
  }

  /**
   * Checks whether the LongLat object `other` is "close to" this LongLat object. "close to" is defined as the
   * Euclidean distance between the two coordinates being less than `CLOSE_TO_DISTANCE_TOLERANCE_IN_DEGREES` (0.00015).
   * @param other The LongLat object used to check whether this object is close to.
   * @return true if this object is "close to" `other`, false otherwise.
   */
  public boolean closeTo(LongLat other) {
    double distanceToCoordinate = distanceTo(other);
    return distanceToCoordinate < CLOSE_TO_DISTANCE_TOLERANCE_IN_DEGREES;
  }

  public LongLat copy() {
    return new LongLat(longitude, latitude);
  }


  public int angleTo(LongLat other) {
    var xDelta = other.longitude - this.longitude;
    var yDelta = other.latitude - this.latitude;
    var angRad = Math.atan2(yDelta, xDelta);
    var angDeg = Math.toDegrees(angRad);
    return (int) Math.round(angDeg / 10.0) * 10;
  }

  /**
   * Returns a LongLat object with coordinates set to the position after moving `DRONE_MOVE_LENGTH_IN_DEGREES` (0.00015)
   * degrees in the direction specified by `angleInDegrees`.
   * @param angleInDegrees the angle in degrees to calculate. 0 is East, 90 is North, 180 is West, and 270
   *                       is south. `SPECIAL_DRONE_HOVERING_ANGLE` (-999) is a special value to indicate the drone is
   *                       hovering (and so its position doesn't change). Except in the special case of the drone
   *                       hovering, `angleInDegrees` should be in multiplies of 10 and in the range of 0 to 350
   *                       (inclusive).
   * @return a LongLat object with coordinates set to the position after moving `DRONE_MOVE_LENGTH_IN_DEGREES` (0.00015)
   * degrees in the direction specified by `angleInDegrees`.
   */
  public LongLat nextPosition(int angleInDegrees) {
    if (angleInDegrees == Drone.SPECIAL_HOVERING_ANGLE) {
      // Special value that specifies drone is hovering.
       return new LongLat(this.longitude, this.latitude);
    }

    // The `DRONE_MOVE_LENGTH_IN_DEGREES` will be the hypotenuse of a right-angled triangle.
    // Hence, we have to calculate the length of the opposite and adjacent side of the triangle to get the change in 
    // longitude and latitude. The length of the opposite side corresponds to the change in latitude, and the length
    // of the adjacent side corresponds to the change in longitude.
    double hypotenuseSideLength = Drone.MOVE_LENGTH_IN_DEGREES;
    double angleInRadians = Math.toRadians(angleInDegrees);
    double oppositeSideLength = hypotenuseSideLength * Math.sin(angleInRadians);
    double adjacentSideLength = hypotenuseSideLength * Math.cos(angleInRadians);
    double nextPositionLongitude = this.longitude + adjacentSideLength;
    double nextPositionLatitude = this.latitude + oppositeSideLength;
    return new LongLat(nextPositionLongitude, nextPositionLatitude);
  }


  /**
   * Checks whether the longitude and latitude coordinates are within the confinement area. The
   * confinement area is defined by the longitudes `CONFINEMENT_AREA_BOUND_WEST_LONGITUDE` and
   * `CONFINEMENT_AREA_BOUND_EAST_LONGITUDE`, and by the latitudes `CONFINEMENT_AREA_BOUND_SOUTH_LATITUDE` and
   * `CONFINEMENT_AREA_BOUND_NORTH_LATITUDE`.
   * @return true if the longitude and latitude coordinates of this object are within the confinement area.
   */
  public boolean isConfined() {
    return (CONFINEMENT_AREA_BOUND_WEST_LONGITUDE <= this.longitude
            && this.longitude <= CONFINEMENT_AREA_BOUND_EAST_LONGITUDE
            && CONFINEMENT_AREA_BOUND_SOUTH_LATITUDE <= this.latitude
            && this.latitude <= CONFINEMENT_AREA_BOUND_NORTH_LATITUDE);
  }


}
