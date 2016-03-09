package freedom.nightq.puzzlepicture.model;

import android.graphics.Bitmap;

/**
 * Created by Nightq on 15/12/10.
 */
public class FilterPicResult {
    public String key;
    public Bitmap result;

    public FilterPicResult(String key, Bitmap result) {
        this.key = key;
        this.result = result;
    }
}
