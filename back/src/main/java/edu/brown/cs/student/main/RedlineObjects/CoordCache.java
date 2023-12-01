package edu.brown.cs.student.main.RedlineObjects;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import edu.brown.cs.student.main.Handlers.RedLineHandler;
import edu.brown.cs.student.main.RedlineObjects.Filterer.Feature;
import edu.brown.cs.student.main.RedlineObjects.Filterer.FeatureCollection;
import edu.brown.cs.student.main.RedlineObjects.Filterer.Geometry;
import edu.brown.cs.student.main.RedlineObjects.Filterer.Property;
import edu.brown.cs.student.main.api.APIResponse;
import edu.brown.cs.student.main.api.BroadbandDatasource;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class CoordCache {
	private final LoadingCache<CoordinateKey, FeatureCollection> cacher;

	/**
	 * Constructor for Cache
	 * @param maxSize - maximum size of cache
	 * @param minutesUntilExpire - minutes until the cache expires
	 * @param filterer - filterer object used to filter the geoJson data
	 */
	public CoordCache(int maxSize, int minutesUntilExpire, Filterer filterer) {

		// Look at the docs -- there are lots of builder parameters you can use
		//   including ones that affect garbage-collection (not needed for Server).
		this.cacher = CacheBuilder.newBuilder()
				// How many entries maximum in the cache?
				.maximumSize(maxSize)
				// How long should entries remain in the cache?
				.expireAfterWrite(minutesUntilExpire, TimeUnit.MINUTES)
				// Keep statistical info around for profiling purposes
				.recordStats()
				.build(
						// Strategy pattern: how should the cache behave when
						// it's asked for something it doesn't have?
						new CacheLoader<>() {
							@Override
							public FeatureCollection load(CoordinateKey key) {
								Double minLat = key.minLat;
								Double minLong = key.minLong;
								Double maxLat = key.maxLat;
								Double maxLong = key.maxLong;
								System.out.println("Loading: " + key);

								return filterer.getFilteredData(minLat, maxLat, minLong, maxLong);
							}
						});
	}

	/**
	 * Function that searches the cache for a target state, county pair.
	 * @param target - The target bounding box we use for the query
	 * @return - the result of the API Call
	 */
	public FeatureCollection search(CoordinateKey target) throws IOException{
		// "get" is designed for concurrent situations; for today, use getUnchecked:
		System.out.println("Retrieving data from cache for key: " + target);
		FeatureCollection result = this.cacher.getUnchecked(target);
		// For debugging and demo (would remove in a "real" version):
		System.out.println(this.cacher.stats());
		return result;
	}
}
