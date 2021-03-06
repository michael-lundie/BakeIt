package io.lundie.michael.bakeit.utilities;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.util.HashMap;
import java.util.Map;

import io.lundie.michael.bakeit.R;

/**
 * Uses code from: https://stackoverflow.com/a/32689039
 * NOTE: A better way to achieve this would be using an AppGlideModule. I'm not sure how to
 * accomplish this yet, so I will come back to it.
 */
public class VideoThumbnailUtility {

    private static final String LOG_TAG = VideoThumbnailUtility.class.getSimpleName();

    private Bitmap bitmap;
    private BitmapDrawable bitmapDrawable = null;

    public void fetchVideoThumbnail(Context context, final Map<Integer, String> bindings, final View view,
                                     final int id, final ProgressBar progressBar,
                                    final ImageView placeholder) {
        for (final Map.Entry<Integer, String> binding : bindings.entrySet()) {
            AppExecutors.getInstance().networkIO().execute(new CallbackRunnable(new RunnableInterface() {
                @Override
                public void onRunCompletion() {
                    ImageView thumbnailView = view.findViewById(binding.getKey());

                    if(bitmap != null) {
                        // Create a new bitmap drawable
                        bitmapDrawable = new BitmapDrawable(
                                context.getResources(), bitmap);
                    }
                        
                    if(bitmapDrawable != null) {
                        CacheManager.getInstance().addBitmapToMemoryCache(
                                id, bitmapDrawable);
                    }

                    //Update our view on the main thread.
                    AppExecutors.getInstance().mainThread().execute(new Runnable() {
                        @Override
                        public void run() {
                            if (bitmapDrawable != null) {
                                //Set the drawable to our fragment thumbnail view
                                thumbnailView.setImageDrawable(bitmapDrawable); 
                            } else {
                                placeholder.setVisibility(View.VISIBLE);
                            }
                            //Show our thumbnail
                            thumbnailView.setVisibility(View.VISIBLE);
                            //Hide the UI progress spinner
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    });
                }
            }) {
                @Override
                public void run() {
                    try {
                        bitmap = retrieveVideoFrameFromVideo(binding.getValue());
                    } catch (Throwable throwable) {
                        throwable.printStackTrace();
                        Log.e(LOG_TAG, throwable.getMessage());
                    }
                    super.run();
                }
            });
        }
    }

    private static Bitmap retrieveVideoFrameFromVideo(String videoPath)throws Throwable
    {
        Bitmap bitmap;
        MediaMetadataRetriever mediaMetadataRetriever = null;
        try
        {
            mediaMetadataRetriever = new MediaMetadataRetriever();

            mediaMetadataRetriever.setDataSource(videoPath, new HashMap<String, String>());

            bitmap = mediaMetadataRetriever.getFrameAtTime(1, MediaMetadataRetriever.OPTION_CLOSEST);
            if (bitmap != null) {
                bitmap = ThumbnailUtils.extractThumbnail(
                        bitmap,
                        300,
                        300,
                        ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new Throwable(
                    "Exception in retriveVideoFrameFromVideo(String videoPath)" + e.getMessage());
        }
        finally
        {
            if (mediaMetadataRetriever != null)
            {
                mediaMetadataRetriever.release();
            }
        }
        return bitmap;
    }
}
