package io.lundie.michael.bakeit.ui.adapters;

import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;

import java.util.HashMap;

// Image Async Downloader code primarily from:
// https://android.jlelse.eu/async-loading-images-on-android-like-a-big-baws-fd97d1a91374
/**
 * Async task for download images.
 */
public class DownloadImageAsync extends AsyncTask<String, Void, Bitmap> {

    private static final String LOG_TAG = DownloadImageAsync.class.getSimpleName();

    private Listener listener;
    DownloadImageAsync(final Listener listener) {
        this.listener = listener;
    }

    public interface Listener {
        void onImageDownloaded(final Bitmap bitmap);
        void onImageDownloadError();
    }

    @Override
    protected Bitmap doInBackground(String... urls) {
        final String url = urls[0];
        Bitmap bitmap = null;
        try {
            bitmap = retrieveVideoFrameFromVideo(url);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return bitmap;
    }
    @Override
    protected void onPostExecute(Bitmap downloadedBitmap) {
        if (null != downloadedBitmap) {
            listener.onImageDownloaded(downloadedBitmap);
        } else {
            listener.onImageDownloadError();
        }
    }

    /**
     * Uses code from: https://stackoverflow.com/a/32689039
     * Find a better way to achieve this. It's not good.
     */
    private static Bitmap retrieveVideoFrameFromVideo(String videoPath)throws Throwable
    {
        Bitmap bitmap = null;
        MediaMetadataRetriever mediaMetadataRetriever = null;
        try
        {
            mediaMetadataRetriever = new MediaMetadataRetriever();

            mediaMetadataRetriever.setDataSource(videoPath, new HashMap<String, String>());

            bitmap = mediaMetadataRetriever.getFrameAtTime(1, MediaMetadataRetriever.OPTION_CLOSEST);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new Throwable("Exception in retriveVideoFrameFromVideo(String videoPath)"+ e.getMessage());
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