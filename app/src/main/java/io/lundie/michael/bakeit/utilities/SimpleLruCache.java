package io.lundie.michael.bakeit.utilities;

import android.support.v4.util.LruCache;

import java.util.ArrayList;

import io.lundie.michael.bakeit.datamodel.models.Recipe;

/**
 * Very simple LRU Cache implementation.
 */
public class SimpleLruCache {
    private LruCache<Object, ArrayList<Recipe>> lru;

    public SimpleLruCache() {
        lru = new LruCache<>(1024);
    }

    public LruCache<Object, ArrayList<Recipe>> getCacheData() {
        return lru;
    }
}
