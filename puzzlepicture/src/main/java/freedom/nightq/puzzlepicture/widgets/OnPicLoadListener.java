package freedom.nightq.puzzlepicture.widgets;

import android.graphics.Bitmap;

/**
 * Created by Nightq on 15/12/29.
 */
public interface OnPicLoadListener {
    void onPicLoad(PolygonImageView view,
                   String path,
                   Bitmap bmp,
                   boolean isSuccess);
}
