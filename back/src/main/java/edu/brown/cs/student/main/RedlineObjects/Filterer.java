package edu.brown.cs.student.main.RedlineObjects;

import com.squareup.moshi.JsonReader;
import com.squareup.moshi.Moshi;
import edu.brown.cs.student.main.Handlers.RedLineHandler;
import okio.BufferedSource;
import okio.Okio;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @param filepath- the filepath of the geoJson data
 */
public class Filterer {
	private String filepath;
	private FeatureCollection allData;
	public Filterer(String filepath) {
		try {
			this.filepath = filepath;
			this.allData = loadInitialData(this.filepath);
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

	/**
	 * Parses the geoJson data into a record object using moshi and 
	 * stores it in a class variable
	 * @param filepath- filepath of the geoJson data
	 */
	public FeatureCollection loadInitialData(String filepath) throws IOException {
		BufferedSource bufferedSource = Okio.buffer(Okio.source(new java.io.File(filepath)));

		JsonReader jsonReader = JsonReader.of(bufferedSource);

		Moshi moshi = new Moshi.Builder().build();

		return moshi.adapter(FeatureCollection.class).fromJson(jsonReader);
	}

	/**
	 * @return getter for full geoJson
	 */
	public FeatureCollection getAllData() {
		return this.allData;
	}

	/**
	 * Gets geoJson data filetered using the keyword
	 * @param keyword the keyword to be filtered
	 * @return the filtered geoJson
	 */
	public FeatureCollection getAreaData(String keyword) {
		FeatureCollection data = this.allData;
		List<Feature> featureResults = new ArrayList<>();

		for (Feature feature: data.features) {
			Property property = feature.properties;
			Map<String, String> descriptionMap = property.area_description_data;

			for (Map.Entry<String, String> entry : descriptionMap.entrySet()) {
				String value = entry.getValue();

				if (value.contains(keyword)) {
					featureResults.add(feature);
					break;
				}
			}
		}

		return new FeatureCollection("FeatureCollection", featureResults);
	}

	/**
	 * Gets geoJson data filtered using the bounding box of four coordinates
	 * @param keyword the keyword to be filtered
	 * @return the filtered geoJson
	 */
	public FeatureCollection getFilteredData(Double minLat, Double maxLat, Double minLong, Double maxLong) {
		FeatureCollection data = this.allData;
		List<Feature> featureResults = new ArrayList<>();

		for (Feature feature: data.features) {
			Geometry geometry = feature.geometry;
			if (geometry == null) {
				continue;
			}

			List<List<List<List<Double>>>> coords = geometry.coordinates;
			boolean isCoordinateWithinRange = true;

			outerloop:
			for (List<List<List<Double>>> nested1: coords) {
				for (List<List<Double>> nested2: nested1) {
					for (List<Double> nested3: nested2) {
						Double lon = nested3.get(0);
						Double lat = nested3.get(1);

						if (!((minLat < lat) && (lat < maxLat)) || !((minLong < lon) && (lon < maxLong))) {
							isCoordinateWithinRange = false;
							break outerloop;
						}
					}
				}
			}

			if (isCoordinateWithinRange) {
				featureResults.add(feature);
			}
		}
		return new FeatureCollection("FeatureCollection", featureResults);
	}

	/**
	 * Custom record for Feature Collection
	 */
	public record FeatureCollection(String type, List<Feature> features) { }

	/**
	 * Custom record for Feature
	 */
	public record Feature(String type, Geometry geometry, Property properties) {}

	/**
	 * Custom record for Geometry field of Feature
	 */
	public record Geometry(String type, List<List<List<List<Double>>>> coordinates) {}

	/**
	 * Custom record for Property field of feature
	 */
	public record Property(Map<String, String> area_description_data, String city, String holc_grade,
						   String holc_id, String name, String neighborhood_id, String state) {}
}



