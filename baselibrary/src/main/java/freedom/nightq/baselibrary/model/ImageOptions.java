package freedom.nightq.baselibrary.model;

import android.text.TextUtils;

/**
 * Created by Nightq on 15/3/20.
 * 存储图片的元信息，可以用来分辨是否是相同的图片。
 */
public class ImageOptions {

    public static final String Type_Picture = "picture";
    public static final String Type_Video = "video";

    /**
     * 判断类型
     * @return
     */
    public static boolean isVideo (String type) {
        return Type_Video.equalsIgnoreCase(type);
    }

    /**
     * 判断类型
     * @return
     */
    public static boolean isPicture (String type) {
        return TextUtils.isEmpty(type)
                || Type_Picture.equalsIgnoreCase(type);
    }

    /**
     * 视频或者照片的文件url: 可能是本地路径或者是网络url
     */
    public String url;

    /**
     * 视频或者照片的宽度
     */
    public int width;
    /**
     * 视频或者照片的高度
     */
    public int height;
    /**
     * 文件的拍照时间
     */
    public long taken_at_gmt;
    /**
     * 数据的md5Hash
     */
    public String md5Hash;
    /**
     * 数据长度
     */
    public long length;
}
