package freedom.nightq.baselibrary.utils;

/**
 * Created by Nightq on 15/4/3.
 */
public class IntegerUtil {

    /**
     * 获取非空值
     * @param oldValue
     * @return
     */
    public static long getValue (Long oldValue) {
        if (oldValue == null) {
            return 0l;
        } else {
            return oldValue;
        }
    }

    /**
     * 获取非空值
     * @param oldValue
     * @return
     */
    public static int getValue (Integer oldValue) {
        if (oldValue == null) {
            return 0;
        } else {
            return oldValue;
        }
    }

}
