package freedom.nightq.baselibrary.utils.imageLoader;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory;
import com.bumptech.glide.module.GlideModule;

/**
 * Created by Nightq on 15/8/7.
 * 如哟需要使用拿需要加
 * <p/>
 * <meta-data android:name="freedom.nightq.baselibrary.utils.imageLoader.GlideModuleForNightQ" android:value="GlideModule" />
 * 到 androidmanifest.xml
 */
public class GlideModuleForNightQ implements GlideModule {

    /** 250 MB of cache. */
    static final int DEFAULT_DISK_CACHE_SIZE = 150 * 1024 * 1024;

    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
        // Apply options to the builder here.
        builder.setDiskCache(
                new InternalCacheDiskCacheFactory(context, DEFAULT_DISK_CACHE_SIZE));
        builder.setDecodeFormat(DecodeFormat.PREFER_ARGB_8888);
    }

    @Override
    public void registerComponents(Context context, Glide glide) {
        // register ModelLoaders here.
    }
}