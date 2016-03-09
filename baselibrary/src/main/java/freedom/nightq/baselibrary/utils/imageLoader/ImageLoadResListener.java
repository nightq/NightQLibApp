package freedom.nightq.baselibrary.utils.imageLoader;

/**
 * Created by Nightq on 15/8/4.
 */
public interface ImageLoadResListener<T> {
    void onSuccess(T bitmap, Integer uri);
    void onFailed(Exception e, Object... objects);
}
