package edu.brown.cs.student.main.api;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;


import java.io.IOException;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.tuple.Pair;

//stencil from sept 19 livecode
/**
 * A class that wraps a LoadingCache instance and caches responses
 * for efficiency.
 * This version uses a Guava cache class to manage the cache.
 */
public class Cache {
  private final LoadingCache<Pair<String,String>, APIResponse> cacher;

  /**
   * Constructor for Cache
   * @param apiCall - the object used to make api calls
   * @param maxSize - maximum size of cache
   * @param minutesUntilExpire - minutes until the cache expires
   */
  public Cache(int maxSize, int minutesUntilExpire, BroadbandDatasource apiCall) {

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
              public APIResponse load(Pair<String,String> key) throws IOException {
                String countyCode = key.getRight();
                String stateCode = key.getLeft();
                System.out.println("called load for: state: "+ stateCode + ", county: " + countyCode);
                return apiCall.getData(key);
              }
            });
  }

  /**
   * Function that searches the cache for a target state, county pair.
   * @param target - The target state county pair we are looking for.
   * @return - the result of the API Call
   */
  public APIResponse search(Pair<String,String> target) throws IOException{
    // "get" is designed for concurrent situations; for today, use getUnchecked:
    APIResponse result = this.cacher.getUnchecked(target);
    // For debugging and demo (would remove in a "real" version):
    System.out.println(this.cacher.stats());
    return result;
  }

}

//TODO: come back to test .contains() for comparing if county matches
