package freedom.nightq.baselibrary.utils;

import android.location.Location;
import android.media.ExifInterface;
import android.text.TextUtils;
import android.text.format.DateFormat;

import net.sourceforge.jheader.App1Header;
import net.sourceforge.jheader.JpegHeaders;
import net.sourceforge.jheader.TagValue;

import java.io.IOException;
import java.text.SimpleDateFormat;

/**
 * Created by H3c on 1/27/15.
 */
public class ExifUtils {
    /**
     * 修改Exif 时间，此方法具耗时，不推荐
     * @param datetime
     * @param filePath
     */
    public static boolean saveExifDate(long datetime, String filePath) {
        boolean result = false;
        try {
            String dateFormat = DateFormat.format("yyyy:MM:dd HH:mm:ss", datetime).toString();
            JpegHeaders jpegHeaders = new JpegHeaders(filePath);
            App1Header exifHeader = jpegHeaders.getApp1Header();
            exifHeader.setValue(App1Header.Tag.DATETIMEORIGINAL, dateFormat);
            jpegHeaders.save(false);
            result = true;
        } catch (Exception e) {
            LogUtils.e("nightq", "saveExifDate error = " + e.getMessage());
            e.printStackTrace();
        }
        return result;
    }

    /** 保存exif中的orientation信息 */
    public static void saveExifOrientation(int exifOrientation, String filePath) throws IOException {
        if(TextUtils.isEmpty(filePath)) return;

        ExifInterface exif = new ExifInterface(filePath);
        exif.setAttribute(ExifInterface.TAG_ORIENTATION, exifOrientation + "");
        exif.saveAttributes();
    }

    /** 保存exif中的location信息 */
    public static void saveExifLocation(double lat, double lon, String filePath) throws IOException {
        //获取jpg文件
        ExifInterface exif = new ExifInterface(filePath);
        //写入纬度信息
        exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE, gpsInfoConvert(lat));
        exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE_REF,
                lat > 0 ?"N" : "S");
        //写入经度信息
        exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, gpsInfoConvert(lon));
        exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF,
                lon > 0 ?"E" : "W");
        exif.saveAttributes();
    }

    /**
     * 获取图片的旋转信息
     * @param filepath
     * @return
     */
    public static int getExifOrientation(String filepath) {
        ExifInterface exif;
        int rotation = 0;
        try {
            exif = new ExifInterface(filepath);
            rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return rotation;
    }

    /**
     * 获取Exif时间
     * @param filePath
     * @return
     */
    public static long getExifDate(String filePath) {
        long date = 0;
        try {
            JpegHeaders jpegHeaders = new JpegHeaders(filePath);
            App1Header exifHeader = jpegHeaders.getApp1Header();
            TagValue tv = exifHeader.getValue(App1Header.Tag.DATETIMEORIGINAL);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
            date = sdf.parse(tv.getAsString()).getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return date;
    }

    private static String gpsInfoConvert(double gpsInfo) {
        gpsInfo = Math.abs(gpsInfo);
        String dms = Location.convert(gpsInfo, Location.FORMAT_SECONDS);
        String[] splits = dms.split(":");
        String[] secnds = (splits[2]).split("\\.");
        String seconds;
        if (secnds.length == 0) {
            seconds = splits[2];
        } else {
            seconds = secnds[0];
        }
        return  splits[0] + "/1," + splits[1] + "/1," + seconds + "/1";
    }
}
