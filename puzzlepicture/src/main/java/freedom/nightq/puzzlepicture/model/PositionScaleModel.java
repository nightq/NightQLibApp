package freedom.nightq.puzzlepicture.model;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;

/**
 * 记录图片的位置缩放信息
 * Created by Nightq on 15/12/16.
 */
public class PositionScaleModel {

    // 缩放和transform
    public float scale = 1f;
    // 用户编辑 和 centercrop 都是以 当前显示的 polygon 的宽高的比例，path 撑满这个 polygon
    private float transformX = 0;
    private float transformY = 0;
    private boolean isTurnRight;// 左右翻转
    private boolean isTurnDown;// 上下翻转
    private int orientation = ExifInterface.ORIENTATION_NORMAL;// 旋转角度

    /**
     * 保存 scale 和 位移
     * @param model
     */
    public void savePosAndScaleModel (PositionScaleModel model) {
        if (model == null) {
            clearTransAndScale();
            return;
        }
        this.scale = model.scale;
        this.transformX = model.transformX;
        this.transformY = model.transformY;
    }

    /**
     * 清空位移缩放
     */
    public void clearTransAndScale () {
        scale = 1f;
        transformX = 0;
        transformY = 0;
    }

    public void setTransformX(float transformX) {
        this.transformX = transformX;
    }

    public void setTransformY(float transformY) {
        this.transformY = transformY;
    }

    /**
     * 通过传入这个显示区域的大小来取得偏移
     * @param visiblePolygonWidth
     * @return
     */
    public float getTransformX(float visiblePolygonWidth) {
        return transformX * visiblePolygonWidth;
    }

    /**
     * 通过传入这个显示区域的大小来取得偏移
     * @param visiblePolygonHeight
     * @return
     */
    public float getTransformY(float visiblePolygonHeight) {
        return transformY * visiblePolygonHeight;
    }

    public void turnRight() {
        isTurnRight = !isTurnRight;
    }
    public boolean isTurnRight() {
        return isTurnRight;
    }
    public void turnDown() {
        isTurnDown = !isTurnDown;
    }
    public boolean isTurnDown() {
        return isTurnDown;
    }
    public void rotate() {
        int o = ExifInterface.ORIENTATION_NORMAL;
        switch (orientation) {
            case ExifInterface.ORIENTATION_NORMAL:
                o = ExifInterface.ORIENTATION_ROTATE_270;
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                o = ExifInterface.ORIENTATION_NORMAL;
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                o = ExifInterface.ORIENTATION_ROTATE_90;
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                o = ExifInterface.ORIENTATION_ROTATE_180;
                break;
        }
        setOrientation(o);
        // 旋转因为会改变宽高所以需要重置用户编辑的缩放和偏移
        clearTransAndScale();
    }

    public void setOrientation(int orientation) {
        this.orientation = orientation;
    }
    public int getOrientation() {
        return orientation;
    }

    /**
     * 获得翻转或旋转后的BMP
     * @param bmp
     * @return
     */
    public Bitmap getProcessBmpWithTurnAndRotate(Bitmap bmp) {
        if(bmp == null || bmp.isRecycled()) return null;
        if (!isTurnRight && !isTurnDown && orientation == ExifInterface.ORIENTATION_NORMAL) {
            return bmp;
        }
        Matrix matrix = getMatrixWithTurnAndRotate();
        return Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, false);
    }

    /**
     * getMatrixWithTurnAndRotate
     * @return
     */
    public Matrix getMatrixWithTurnAndRotate () {
        Matrix matrix = new Matrix();
        matrix.setScale(isTurnRight ? -1 : 1, isTurnDown ? -1 : 1);
        int rotate = 0;
        if(orientation == ExifInterface.ORIENTATION_ROTATE_90) {
            rotate = 90;
        } else if(orientation == ExifInterface.ORIENTATION_ROTATE_180) {
            rotate = 180;
        } else if(orientation == ExifInterface.ORIENTATION_ROTATE_270) {
            rotate = 270;
        }
        matrix.postRotate(rotate);
        return matrix;
    }

}
