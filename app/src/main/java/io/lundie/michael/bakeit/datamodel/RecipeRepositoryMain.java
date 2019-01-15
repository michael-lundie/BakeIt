package io.lundie.michael.bakeit.datamodel;

import android.arch.lifecycle.MutableLiveData;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.inject.Inject;

import io.lundie.michael.bakeit.datamodel.models.Recipe;
import io.lundie.michael.bakeit.utilities.AppConstants;
import io.lundie.michael.bakeit.utilities.AppExecutors;
import io.lundie.michael.bakeit.utilities.AssetProvider;
import io.lundie.michael.bakeit.utilities.CallbackRunnable;
import io.lundie.michael.bakeit.utilities.RunnableInterface;
import io.lundie.michael.bakeit.utilities.SimpleLruCache;

/**
 * Recipe Repository class responsible for the fetching and management of recipe data.
 */
public class RecipeRepositoryMain implements RecipeRepository {

    private static final String LOG_TAG = RecipeRepositoryMain.class.getSimpleName();

    private final Gson gson;
    private final AssetProvider assetProvider;
    private SimpleLruCache lruCache;

    private static MutableLiveData<ArrayList<Recipe>> recipesLiveData;
    ArrayList<Recipe> recipes = null;

    @Inject
    public RecipeRepositoryMain(Gson gson, AssetProvider assetProvider, SimpleLruCache lruCache) {
        this.gson = gson;
        this.assetProvider = assetProvider;
        this.lruCache = lruCache;
    }


    @Override
    public MutableLiveData<ArrayList<Recipe>> getRecipes() {

        if (recipesLiveData == null) {
            recipesLiveData = new MutableLiveData<>();
        }

        if(recipesLiveData.getValue() == null || recipesLiveData.getValue().isEmpty()) {
            if(!attemptCacheRetrieval()) {
                //TODO: Testing - implement JSON retrieval only.
                //retrieveFromJSON();
                fetchRecipesOverNetwork();
            }

        }
        return recipesLiveData;
    }

    private void fetchRecipesOverNetwork() {
        RunnableInterface recipesFetchRunInterface = new RunnableInterface() {
            @Override
            public void onRunCompletion() {
                // Note that we should never have a null value returned here.
                recipesLiveData.postValue(recipes);
                sendToCache(recipes);
            }
        };

        AppExecutors.getInstance().networkIO().execute(new CallbackRunnable(recipesFetchRunInterface) {
            @Override
            public void run() {
                String urlString = AppConstants.RECIPES_URL;
                if (!TextUtils.isEmpty(urlString)) {
                    URL url = createUrl(urlString);
                    try {
                        // Everything is a-okay. Continue to fetch results.
                        ArrayList<Recipe> resultItems = getRecipesFromStream(url);

                        if (resultItems != null) {
                            // Fetch results are not null. Assign to our return variable.
                            recipes = resultItems;
                        } else {
                            throw new IOException("No response received.");
                        }
                    } catch(Exception e) {
                        Log.e("Log error", "Problem with Requested URL", e);
                    }
                }
                super.run();
            }
        });
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private ArrayList<Recipe> getRecipesFromStream(URL url) throws IOException {
        ArrayList<Recipe> recipes = null;

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                Log.v(LOG_TAG, ">>>>>>>>>>>>>>>>>>>>FETCHING FROM NETWORK <<<<<<<<<<<<<<<<<<<<<<<<");
                inputStream = urlConnection.getInputStream();
                Reader reader = new InputStreamReader(inputStream, "UTF-8");
                recipes = convertStreamWithGson(reader);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the Google Books JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return recipes;
    }

    private ArrayList<Recipe> convertStreamWithGson(Reader reader) {
        Type recipeListType = new TypeToken<ArrayList<Recipe>>(){}.getType();
        return gson.fromJson(reader, recipeListType);
    }


    private void retrieveFromJSON() {
        //TODO: Put this into a seperate thread
        Reader reader = new InputStreamReader(assetProvider.getJsonFile());

        Type recipeListType = new TypeToken<ArrayList<Recipe>>(){}.getType();

        ArrayList<Recipe> recipeList = gson.fromJson(reader, recipeListType);

        sendToCache(recipeList);

        recipesLiveData.setValue(recipeList);
    }

    private void sendToCache(ArrayList<Recipe> recipes) {
        lruCache.getCacheData().put("recipe", recipes);
    }

    private boolean attemptCacheRetrieval() {
        Log.i(LOG_TAG, "TEST: Retrieving from cache");
        ArrayList<Recipe> recipeList = (ArrayList<Recipe>) lruCache.getCacheData().get("recipe");
        if(recipeList != null && !recipeList.isEmpty()) {
            recipesLiveData.setValue(recipeList);
            return true;
        } return false;
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error with creating URL ", e);
        }
        return url;
    }
}