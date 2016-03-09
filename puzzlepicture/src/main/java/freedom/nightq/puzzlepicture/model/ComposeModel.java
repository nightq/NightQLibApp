package freedom.nightq.puzzlepicture.model;

import android.graphics.Path;
import android.text.TextUtils;

import freedom.nightq.baselibrary.NightQAppLib;
import freedom.nightq.baselibrary.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 版式
 * Created by Nightq on 15/12/5.
 */
public class ComposeModel {

    public enum PercentUnit {
        // 百分比
        PercentH,
        // 千分比
        PercentT
    }

//      "drawable_name": "",
    public String drawable_name;
    //
//    int resId = context.getResources().getIdentifier(
//            Compose_File_2, //Compose_File,
//            "raw",
//            context.getPackageName());
    // 底部实例图片id
    private int drawableId;
    // 蒙板图片id
    private int frameDrawableId;
    // 各个框坐标
    public float[][] polygons;
//      "polygons": [
//              point1      point2      point3      point4
//              [10, 10,    90.5, 10,   80, 85,     15, 95],
//              [15, 7,     70, 4,      95, 95,     4, 75]
//              ]

    private ConcurrentMap<String, List<Path>> polygonsPath;

    /**
     * get drawable id
     * @return
     */
    public int getDrawableId() {
        if (drawableId != -1 && drawableId != 0) {
            return drawableId;
        }
        if (TextUtils.isEmpty(drawable_name)) {
            return -1;
        }
        try {
            drawableId = NightQAppLib.getAppContext().getResources().getIdentifier(
                    drawable_name, //Compose_File,
                    "mipmap",
                    NightQAppLib.getAppContext().getPackageName());
        } catch (Exception e) {

        }
        return drawableId;
    }

    /**
     * get drawable id
     * @return
     */
    public int getFrameDrawableId() {
        if (frameDrawableId != -1 && frameDrawableId != 0) {
            return frameDrawableId;
        }
        if (TextUtils.isEmpty(drawable_name)) {
            return -1;
        }
        try {
            frameDrawableId = NightQAppLib.getAppContext().getResources().getIdentifier(
                    "frame_" + drawable_name, //Compose_File,
                    "mipmap",
                    NightQAppLib.getAppContext().getPackageName());
        } catch (Exception e) {

        }
        return frameDrawableId;
    }


    /**
     *
     * @param width
     * @param height
     * @return
     */
    private String getPolygonKey (float width, float height) {
        return ((int)width) + "*" + ((int)height);
    }

    /**
     * 获取对应大小的版式框
     * @param width
     * @param height
     * @return
     */
    public synchronized List<Path> getPolygonsPath(float width, float height) {
        if (polygons == null || width == 0 || height == 0) {
            throw new IllegalArgumentException();
        }
        if (polygonsPath != null
                && polygonsPath.containsKey(getPolygonKey(width, height))) {
            return polygonsPath.get(getPolygonKey(width, height));
        }
        if (polygonsPath == null) {
            polygonsPath = new ConcurrentHashMap<>();
        }
        ArrayList<Path> result = new ArrayList<>();
        Path tmp;
        float[] polygonArray;
        for (int i = 0; i < polygons.length; i++) {
            tmp = new Path();
            polygonArray = polygons[i];
            tmp.moveTo(
                    sizeFromPercent(polygonArray[0], width),
                    sizeFromPercent(polygonArray[1], height));
            for (int p = 2; p <= polygonArray.length/2; p++) {
                tmp.lineTo(
                        sizeFromPercent(polygonArray[p*2-2], width),
                        sizeFromPercent(polygonArray[p*2-1], height));
            }
            tmp.close();
            result.add(tmp);
        }
        polygonsPath.put(getPolygonKey(width, height), result);
        return result;
    }

    /**
     * 获取这个版式的其中一个框，得到的框是铺满这个target 框的，至少有一边是撑满的
     * @param targetWidth 框的宽度
     * @param targetHeight 框的高度
     * @param position 第几个框
     * @return
     */
    public Path getPolygonPath(float targetWidth, float targetHeight, int position) {
        if (polygons == null || targetWidth <= 0 || targetHeight <= 0
                || position >= polygons.length
                || position < 0) {
            LogUtils.e("nightq", "polygons = " + polygons
                    + " targetWidth = " + targetWidth
                    + " targetHeight = " + targetHeight
                    + " position = " + position);
            throw new IllegalArgumentException();
        }
        Path result;
        float[] polygonArray = polygons[position];

        // x的最小为left
        float left = Math.min(Math.min(polygonArray[0], polygonArray[2]), Math.min(polygonArray[4], polygonArray[6]));
        // x的最大为right
        float right = Math.max(Math.max(polygonArray[0], polygonArray[2]), Math.max(polygonArray[4], polygonArray[6]));
        // y的最小为top
        float top = Math.min(Math.min(polygonArray[1], polygonArray[3]), Math.min(polygonArray[5], polygonArray[7]));
        // y的最大为bottom
        float bottom = Math.max(Math.max(polygonArray[1], polygonArray[3]), Math.max(polygonArray[5], polygonArray[7]));

        float pathWidth = Math.abs(right - left);
        float pathHeight = Math.abs(bottom - top);

        // 缩放之后的偏移
        float offsetX = 0;
        float offsetY = 0;

        // 把原来的这个框变成 target 框 的size
        float pathScale;

        if (targetWidth/pathWidth > targetHeight/pathHeight) {
            pathScale = targetHeight/pathHeight;
            // 路径比目标显示框还高
            offsetX = Math.abs((targetWidth - pathWidth*pathScale)/2);
        } else {
            pathScale = targetWidth/pathWidth;
            // 路径比目标显示框还宽
            offsetY = Math.abs((targetHeight - pathHeight*pathScale)/2);
        }
        result = new Path();
//        result.moveTo(
//                sizeFromPercent(pathScale*(polygonArray[0]-left)+offsetX, targetWidth),
//                sizeFromPercent(pathScale*(polygonArray[1]-top)+offsetY, targetHeight));
        result.moveTo(
                pathScale*(polygonArray[0]-left)+offsetX,
                pathScale*(polygonArray[1]-top)+offsetY);
        for (int p = 2; p <= polygonArray.length/2; p++) {
//            result.lineTo(
//                    sizeFromPercent(pathScale*(polygonArray[p*2-2]-left)+offsetX, targetWidth),
//                    sizeFromPercent(pathScale*(polygonArray[p*2-1]-top)+offsetY, targetHeight));
            result.lineTo(
                    pathScale*(polygonArray[p*2-2]-left)+offsetX,
                    pathScale*(polygonArray[p*2-1]-top)+offsetY);
        }
        result.close();
        return result;
    }


    /**
     *
     * @param total
     * @param percent
     * @return
     */
    public float sizeFromPercent (float total, float percent) {
        return sizeFromPercent(PercentUnit.PercentH, total, percent);
    }

    /**
     *
     * @param total
     * @param percent
     * @return
     */
    public float sizeFromPercent (PercentUnit percentUnit, float total, float percent) {
        switch (percentUnit) {
            case PercentH:
                return total * percent / 100;
            case PercentT:
            default:
                return total * percent / 1000;
        }
    }
}
