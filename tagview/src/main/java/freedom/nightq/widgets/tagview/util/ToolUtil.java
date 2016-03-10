package freedom.nightq.widgets.tagview.util;

import android.content.res.Resources;

/**
 * Created by Nightq on 16/3/2.
 */
public class ToolUtil {

    public static final int Angle_0 = 0;
    public static final int Angle_45 = 45;
    public static final int Angle_135 = 135;
    public static final int Angle_180 = 180;
    public static final int Angle_M_135 = 275;
    public static final int Angle_M_45 = 315;

    public static final int[] Angle_Array = {
            Angle_0,
            Angle_45,
            Angle_135,
            Angle_180,
            Angle_M_135,
            Angle_M_45
    };


    /**
     * 统一为 0 － 360
     * @param angle
     * @return
     */
    public static int unionAngle (int angle) {
        return (angle + 360) % 360;
    }

    /**
     * get dimen
     * @param resources
     * @param id
     * @return
     */
    public static int getDimen(Resources resources, int id) {
        return (int)resources.getDimension(id);
    }

    /**
     * get
     * @param resources
     * @param id
     * @return
     */
    public static int getColor(Resources resources, int id) {
        return resources.getColor(id);
    }
}
