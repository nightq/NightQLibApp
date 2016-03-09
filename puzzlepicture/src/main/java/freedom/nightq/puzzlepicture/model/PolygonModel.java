package freedom.nightq.puzzlepicture.model;

import android.graphics.Path;

/**
 * Created by Nightq on 15/12/21.
 */
public class PolygonModel {

    public Path mPath;
    public float rectFWidth;
    public float rectFHeight;


    public PolygonModel(Path mPath, float rectFWidth, float rectFHeight) {
        this.mPath = mPath;
        this.rectFWidth = rectFWidth;
        this.rectFHeight = rectFHeight;
    }
}
