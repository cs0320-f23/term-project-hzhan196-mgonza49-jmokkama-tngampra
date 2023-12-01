package edu.brown.cs.student.main.RedlineObjects;

import java.util.Objects;

/**
 * Custom class that defines the bounding box to be used for the filtered query
 */
public class CoordinateKey {
	public Double minLat;
	public Double maxLat;
	public Double minLong;
	public Double maxLong;

	/**
   * Constructor for CoordinateKey
   * @param minLat - the coordinate corresponding to minimum latitude
   * @param maxLat - maximum latitude coordinate for the bounding box
   * @param minLong - minimum longtitude coordinate for the bounding box
   * @param maxLong - maximum longtitude coordinate for the bounding box
   */

	public CoordinateKey(Double minLat, Double maxLat, Double minLong, Double maxLong) {
		this.minLat = minLat;
		this.maxLat = maxLat;
		this.minLong = minLong;
		this.maxLong = maxLong;
	}

	/**
	 * Method that compares equality between Coordinate Key objects because normal
	 * equals() methods does not work.
	 */
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		CoordinateKey other = (CoordinateKey) o;
		return Objects.equals(minLat, other.minLat) &&
				Objects.equals(maxLat, other.maxLat) &&
				Objects.equals(minLong, other.minLong) &&
				Objects.equals(maxLong, other.maxLong);
	}

	/**
	 * Method that returns the hashCode for the object.
	 */
	@Override
	public int hashCode() {
		return Objects.hash(minLat, maxLat, minLong, maxLong);
	}
}