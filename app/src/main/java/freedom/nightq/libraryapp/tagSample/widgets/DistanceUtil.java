package freedom.nightq.libraryapp.tagSample.widgets;

import android.graphics.PointF;

/**
 * Created by Nightq on 16/3/3.
 */
public class DistanceUtil {

    /**
     * 是同一个点
     * @return
     */
    public static boolean isSamePoint (PointF point1, PointF point2) {
        if (Float.compare(Math.abs(point1.x-point2.x), 0.005f) < 0
                && Float.compare(Math.abs(point1.y-point2.y), 0.005f) < 0) {
            return true;
        }
        return false;
    }
}
