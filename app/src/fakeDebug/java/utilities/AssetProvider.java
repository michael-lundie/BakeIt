package io.lundie.michael.bakeit.utilities;

import android.app.Application;
import android.renderscript.ScriptGroup;

import java.io.IOException;
import java.io.InputStream;

import javax.inject.Inject;

/**
 * Asset Provider classed used for testing. Allows access to backing.json files.
 */
public class AssetProvider {

    private Application applicationContext;

    @Inject
    public AssetProvider(Application application) {
        this.applicationContext = application;
    }

    /**
     * Simple class which returns the input stream for our json file
     * @return InputStream or null
     */
    public InputStream getJsonFile() {

        InputStream jsonData = null;

        try {
            jsonData = applicationContext.getAssets().open("baking.json");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jsonData;
    }
}
