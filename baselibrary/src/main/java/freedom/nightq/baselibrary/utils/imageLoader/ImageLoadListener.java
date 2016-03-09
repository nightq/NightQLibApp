package freedom.nightq.baselibrary.utils.imageLoader;

/**
 * Created by Nightq on 15/8/4.
 */
public interface ImageLoadListener<T> {
    void onSuccess(T bitmap, String uri);
    void onFailed(Exception e, Object... objects);
}
