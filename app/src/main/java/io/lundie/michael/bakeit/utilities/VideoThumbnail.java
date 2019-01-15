package io.lundie.michael.bakeit.utilities;

import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.android.exoplayer2.C;

import java.util.HashMap;

/**
 * Uses code from: https://stackoverflow.com/a/32689039
 * Find a better way to achieve this. It's not good.
 */
public class VideoThumbnail {

    private String videoPath;
    private ImageView imageView;
    private ProgressBar progressBar;
    private Bitmap bitmap;

    public VideoThumbnail(String videoPath, ImageView bindingImageView, ProgressBar progressBar) {
        this.imageView = bindingImageView;
        this.videoPath = videoPath;
        this.progressBar = progressBar;
        fetchVideoThumbnail();
    }

    private void fetchVideoThumbnail() {

        AppExecutors.getInstance().networkIO().execute(new CallbackRunnable(new RunnableInterface() {
            @Override
            public void onRunCompletion() {
                if(bitmap != null) {
                    AppExecutors.getInstance().mainThread().execute(new Runnable() {
                        @Override
                        public void run() {
                            imageView.setImageBitmap(bitmap);
                            progressBar.setVisibility(View.GONE);
                        }
                    });
                }
            }
        }) {
            @Override
            public void run() {
                try {
                    progressBar.setVisibility(View.VISIBLE);
                    bitmap = retrieveVideoFrameFromVideo(videoPath);
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
                super.run();
            }
        });
    }

    public static Bitmap retrieveVideoFrameFromVideo(String videoPath)throws Throwable
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