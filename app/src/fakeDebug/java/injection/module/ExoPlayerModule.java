package injection.module;

import android.app.Application;
import android.content.Context;
import android.net.Uri;
import android.os.Handler;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.google.android.exoplayer2.util.Util;

import dagger.Module;
import dagger.Provides;
import io.lundie.michael.bakeit.R;

/**
 * Returns various (injection) instances required for Expo Player functionality
 */
@Module
public class ExoPlayerModule {

    @Provides
    public TrackSelector providesTrackSelector() {
        return new DefaultTrackSelector();
    }

    @Provides
    public LoadControl providesLoadControl() {
        return new DefaultLoadControl();
    }

    @Provides
    public SimpleExoPlayer providesExoPlayerInstance(Application context,
                                                     TrackSelector trackSelector,
                                                     LoadControl loadControl) {
        return ExoPlayerFactory.newSimpleInstance(context,
                new DefaultRenderersFactory(context), trackSelector, loadControl);
    }


    @Provides
    public String providesUserAgent(Application context) {
        return Util.getUserAgent(context, context.getString(R.string.app_name));
    }

    @Provides
    public HttpDataSource.Factory providesHttpDataSource(
            String userAgent, DefaultBandwidthMeter bandwidthMeter) {
        return new DefaultHttpDataSourceFactory(userAgent, bandwidthMeter);
    }

    @Provides
    public DataSource.Factory providesDataSource(
            Context context, DefaultBandwidthMeter bandwidthMeter, HttpDataSource.Factory httpDataSource) {
        return new DefaultDataSourceFactory(context, bandwidthMeter, httpDataSource);
    }

    @Provides
    public ExtractorMediaSource.Factory provideExtractorMediaSourceFactory(DataSource.Factory dataSourceFactory) {
        return new ExtractorMediaSource.Factory(dataSourceFactory);
    }
}
