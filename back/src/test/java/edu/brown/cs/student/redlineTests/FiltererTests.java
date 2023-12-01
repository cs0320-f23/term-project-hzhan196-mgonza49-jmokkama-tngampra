package edu.brown.cs.student.redlineTests;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import edu.brown.cs.student.main.RedlineObjects.Filterer;
import spark.Spark;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Filter;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
 
public class FiltererTests {

	@BeforeAll
	public static void setupOnce() {

	}

	@Test
	public void getAllData() {
		Filterer filterer = new Filterer("data/geodata/mockDownload.json");

		Filterer.FeatureCollection response = filterer.getAllData();

		assertEquals("FeatureCollection", response.type());

		List<Filterer.Feature> featList = response.features();
		Filterer.Feature firstFeature = featList.get(0);
		assertEquals("Feature", firstFeature.type());

		Filterer.Geometry geometry = firstFeature.geometry();
		assertEquals("MultiPolygon", geometry.type());
		List<List<Double>> actualCoords = new ArrayList<>();
		List<Double> dimension1 = new ArrayList<>();
		dimension1.add(-73.5);
		dimension1.add(41.1);
		actualCoords.add(dimension1);

		List<List<Double>> resCoords = new ArrayList<>();

		List<List<List<List<Double>>>> coords = geometry.coordinates();
		for (List<List<List<Double>>> c1 : coords) {
			for (List<List<Double>> c2 : c1) {
				resCoords.addAll(c2);
			}
		}

		for (int i = 0; i < resCoords.size(); i++) {
			assertEquals(resCoords.get(i).get(0), actualCoords.get(i).get(0));
			assertEquals(resCoords.get(i).get(1), actualCoords.get(i).get(1));
		}

		assertEquals(resCoords.size(), 1);
	}

	@Test
	public void getFiltered() {
		Filterer filterer = new Filterer("data/geodata/mockDownload.json");

		Filterer.FeatureCollection response = filterer.getFilteredData(40.1, 50.1, -80.1, -70.1);

		assertEquals("FeatureCollection", response.type());

		List<Filterer.Feature> featList = response.features();
		Filterer.Feature firstFeature = featList.get(0);
		assertEquals("Feature", firstFeature.type());

		Filterer.Geometry geometry = firstFeature.geometry();
		assertEquals("MultiPolygon", geometry.type());
		List<List<Double>> actualCoords = new ArrayList<>();
		List<Double> dimension1 = new ArrayList<>();
		dimension1.add(-73.5);
		dimension1.add(41.1);
		actualCoords.add(dimension1);

		List<List<Double>> resCoords = new ArrayList<>();

		List<List<List<List<Double>>>> coords = geometry.coordinates();
		for (List<List<List<Double>>> c1 : coords) {
			for (List<List<Double>> c2 : c1) {
				resCoords.addAll(c2);
			}
		}

		for (int i = 0; i < resCoords.size(); i++) {
			assertEquals(resCoords.get(i).get(0), actualCoords.get(i).get(0));
			assertEquals(resCoords.get(i).get(1), actualCoords.get(i).get(1));
		}

		assertEquals(resCoords.size(), 1);
	}

	@Test
	public void getFilteredOutOfBounds() {
		Filterer filterer = new Filterer("data/geodata/mockDownload.json");

		Filterer.FeatureCollection response = filterer.getFilteredData(50.05, 50.1, -80.1, -70.1);

		assertEquals("FeatureCollection", response.type());

		List<Filterer.Feature> featList = response.features();
		assertTrue(featList.isEmpty());
	}

	@Test
	public void getFilteredByArea() {
		Filterer filterer = new Filterer("data/geodata/mockDownload.json");

		Filterer.FeatureCollection response = filterer.getAreaData("Stamford");

		assertEquals("FeatureCollection", response.type());

		List<Filterer.Feature> featList = response.features();
		Filterer.Feature firstFeature = featList.get(0);
		assertEquals("Feature", firstFeature.type());

		Filterer.Geometry geometry = firstFeature.geometry();
		assertEquals("MultiPolygon", geometry.type());
		List<List<Double>> actualCoords = new ArrayList<>();
		List<Double> dimension1 = new ArrayList<>();
		dimension1.add(-73.5);
		dimension1.add(41.1);
		actualCoords.add(dimension1);

		List<List<Double>> resCoords = new ArrayList<>();

		List<List<List<List<Double>>>> coords = geometry.coordinates();
		for (List<List<List<Double>>> c1 : coords) {
			for (List<List<Double>> c2 : c1) {
				resCoords.addAll(c2);
			}
		}

		for (int i = 0; i < resCoords.size(); i++) {
			assertEquals(resCoords.get(i).get(0), actualCoords.get(i).get(0));
			assertEquals(resCoords.get(i).get(1), actualCoords.get(i).get(1));
		}

		assertEquals(resCoords.size(), 1);
	}

	@Test
	public void keywordNotInData() {
		Filterer filterer = new Filterer("data/geodata/mockDownload.json");

		Filterer.FeatureCollection response = filterer.getAreaData("foo");

		assertEquals("FeatureCollection", response.type());

		List<Filterer.Feature> featList = response.features();
		assertTrue(featList.isEmpty());
	}
}
