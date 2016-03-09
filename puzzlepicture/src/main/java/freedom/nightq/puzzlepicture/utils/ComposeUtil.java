package freedom.nightq.puzzlepicture.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.graphics.RectF;
import android.text.TextUtils;

import java.io.File;
import java.util.List;

import freedom.nightq.baselibrary.NightQAppLib;
import freedom.nightq.baselibrary.utils.DeviceUtils;
import freedom.nightq.baselibrary.utils.LogUtils;
import freedom.nightq.baselibrary.utils.PhotoUtils;
import freedom.nightq.baselibrary.utils.ProcessPicFileUtil;
import freedom.nightq.baselibrary.utils.imageLoader.NightQImageLoader;
import freedom.nightq.puzzlepicture.R;
import freedom.nightq.puzzlepicture.model.PositionScaleModel;
import freedom.nightq.puzzlepicture.model.ProcessComposeModel;
import freedom.nightq.puzzlepicture.model.ProcessPicModel;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by Nightq on 15/12/16.
 */
public class ComposeUtil {

    /**
     * 生成的图的大小
     */
    public static final int TargetComposeWidth = 1280;
    public static final int TargetComposeHeight = 1280;

    public static int ComposeWidth = -1;
    /**
     * 返回编辑版式的地方的尺寸
     * @return
     */
    public static int getComposeWidth () {
        if (ComposeWidth <= 0) {
            ComposeWidth = DeviceUtils.screenWPixels - DeviceUtils.dpToPx(20);
        }
        return ComposeWidth;
    }

    /**
     * 显示小一点的
     * @return
     */
    public static int getComposeVisibleWidth () {
        return getComposeWidth() * 3 / 4;
    }

    /**
     * 版式宽度
     */
    /**
     * 计算将bitmap缩放到与view等宽或等高后的大小
     * @param viewHeight
     * @param viewWidth 当前显示的polygon
     * @return PositionScaleModel 返回的位置是 图片centercrop 的缩放和pivot percent
     * @return
     */
    public static PositionScaleModel getCenterCropModel(
            float viewWidth, float viewHeight,
            int bmpWidth, int bmpHeight) {
        if (viewHeight <= 0
                || viewWidth <= 0
                || bmpWidth <= 0
                || bmpHeight <= 0) {
            return null;
        }
        PositionScaleModel model = new PositionScaleModel();

        final float widthScale = viewWidth / (float) bmpWidth;
        final float heightScale = viewHeight / (float) bmpHeight;

        model.scale = Math.max(widthScale, heightScale);

        // centercrop 不移动
        model.setTransformX((viewWidth - bmpWidth * model.scale) / 2F / viewWidth);
        model.setTransformY((viewHeight - bmpHeight * model.scale) / 2F /viewHeight);

        return model;
    }

    /**
     * 获取当前显示的图片偏移和scale
     * getDisplayRect  这是bitmap 缩放之后的大小，比如1920*1080 的图。
     *                      显示到900*540上面，那么这个图会缩放到960*540.然后显示到900*540 这个上。
     *                      displayHeight＝540 displayWidth＝960.
     * @param viewWidth
     * @param viewHeight  这是显示的view 区域的大小。就是上例中的 900*540
     * @return
     */
    public static PositionScaleModel getCurrentPosScaModel (
            PhotoViewAttacher mAttacher,
            float viewWidth, float viewHeight) {
        // 显示的 rect
        RectF imageRect = mAttacher.getDisplayRect();
        if (imageRect == null) {
            return null;
        }

        if (Float.compare(imageRect.width(), 0f) <= 0
                || Float.compare(imageRect.height(), 0f) <= 0) {
            return null;
        }
        // 这里应该是相等的
        return getCurrentPosScaModel(
                viewWidth, viewHeight,
                imageRect.width(), imageRect.height(),
                imageRect.left, imageRect.top, mAttacher.getScale());
    }

    /**
     * 获取centercrop 之后的 scale  和 transform
     * @param displayWidth
     * @param displayHeight 这是bitmap 先 centercrop 然后 缩放之后的大小，比如1920*1080 的图。
     *                      显示到900*540上面，那么这个图会缩放到960*540.然后用户可以缩放他到合适的大小也就是这个大小
     *                      origin bmp(原图) * centercrop scale（居中） == centercrop bmp;
     *                      displayWidth displayHeight（显示的时候的图大小） == centercrop bmp（居中的图） * scale（用户缩放）
     * @param viewWidth
     * @param viewHeight  这是显示的view 区域的大小。就是上例中的 900*540
     * @return PositionScaleModel 返回的位置是 图片centercrop 之后的 缩放和pivot percent
     */
    private static PositionScaleModel getCurrentPosScaModel (
            float viewWidth, float viewHeight,
            float displayWidth, float displayHeight,
            float left, float top,
            float scale) {
        if (viewWidth <= 0 || viewHeight <= 0) {
            return null;
        }
        if (Float.compare(displayWidth, 0f) <= 0 || Float.compare(displayHeight, 0f) <= 0) {
            return null;
        }

        // 移动后的中心
        float transformedX = left + displayWidth / 2;
        float transformedY = top + displayHeight / 2;

        // 初始位置
        float initX = viewWidth/2;
        float initY = viewHeight/2;

        // 先scale 然后 transform
        PositionScaleModel posHolder = new PositionScaleModel();
        // 结果位置减去初始位置 就是偏移距离
        posHolder.setTransformX((transformedX - initX)/viewWidth);
        posHolder.setTransformY((transformedY - initY)/viewHeight);
        posHolder.scale = scale;

        return posHolder;
    }


    /**
     * check model is valid
     * @return
     */
    public static boolean checkValidComposeModel (ProcessComposeModel model) {
        if (model != null
                && model.mPicList != null
                && model.mPicList.size() > 0
                && (model.mPicList.size() == 1 || model.mComposeModel != null)) {
            return true;
        }
        return false;
    }

//    public static final int[] Color =
//            {
//                    android.graphics.Color.BLUE,
//                    android.graphics.Color.GREEN,
//                    android.graphics.Color.RED,
//                    android.graphics.Color.YELLOW
//            };
    /**
     * 生成组合大图
     * @return
     */
    public static String generateComposePics (
            ProcessComposeModel model,
            int targetWidth,
            int targetHeight,
            String saveFilePath) {
        if (!checkValidComposeModel(model)) {
            return null;
        }
        // get  pic 取图片
        List<ProcessPicModel> mPicList =  model.mPicList;
        // get path 取各个框
        List<Path> mDisplayPaths =
                model.mComposeModel
                        .getPolygonsPath(targetWidth, targetHeight);
//        // check path  and pic
        if (mDisplayPaths == null
                || mDisplayPaths.size() != mPicList.size()) {
            return null;
        }
        Bitmap mResultBmp = null;
        try {
            mResultBmp = Bitmap.createBitmap(
                    targetWidth, targetHeight, Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError e) {

        }
        if (mResultBmp == null) {
            return null;
        }
        Canvas canvas = new Canvas(mResultBmp);
        // 抗锯齿
        canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG| Paint.FILTER_BITMAP_FLAG));
        RectF mRectF;
        Path mPath;
        int picCount = mPicList.size();
        for (int i = 0; i < picCount; i++) {
            mRectF = new RectF();
            mPath = mDisplayPaths.get(i);
            mPath.computeBounds(mRectF, true);
            if (!drawProcessPicModelToBmp(
//                    Color[i],
                    canvas,
                    mPicList.get(i),
                    mPath,
                    mRectF)) {
                return null;
            }
        }
//        long start = System.currentTimeMillis();
        // 画蒙板
        drawCover(canvas, model);
        // 保存要发布的图片
        String path = ProcessPicFileUtil
                .getInstance()
                .createCacheFile("processTmp"+ System.currentTimeMillis());
        boolean result = PhotoUtils.saveBitmapToFile(mResultBmp, path, false);

//        LogUtils.e("nightq", "保存合成的图时间 ＝ " + (System.currentTimeMillis() - start));
//        start = System.currentTimeMillis();

        // 加水印，保存到相册
        if(!TextUtils.isEmpty(saveFilePath)) {
            try {
                drawWaterPrint(canvas);
                File toFile = new File(saveFilePath);
                if (PhotoUtils.saveBitmapToFile(mResultBmp, toFile.getPath(), false)) {
//                    String localPath = toFile.getAbsolutePath();
//                    PhotoUtils.updateGallery(localPath);
                }
            } catch (Exception e) {}
        }

        LogUtils.e("nightq", "保存到相册 ＝ " + path);
        if (result) {
            return path;
        } else {
            return null;
        }
    }

    /**
     * 画蒙板
     * @param canvas
     * @param model
     * @return
     */
    private static boolean drawCover (
            Canvas canvas,
            ProcessComposeModel model) {
        int resId = model.mComposeModel.getFrameDrawableId();
        if (resId <= 0) {
            return true;
        }
        Bitmap source = NightQImageLoader.loadBitmapById(resId);
        if (source != null && !source.isRecycled()) {
            Matrix baseMatrix = new Matrix();
            baseMatrix.setScale(
                    canvas.getWidth() / (float)source.getWidth(),
                    canvas.getHeight() / (float)source.getHeight());
            canvas.drawBitmap(source, baseMatrix, null);
            return true;
        } else {
            return false;
        }
    }

    /**
     * 画水印
     * @param canvas
     * @return
     */
    private static boolean drawWaterPrint (
            Canvas canvas) {
        Bitmap source = NightQImageLoader.loadBitmapById(R.mipmap.image_waterprint);
        if (source != null && !source.isRecycled()) {
            Matrix baseMatrix = new Matrix();
            if (canvas.getWidth() < source.getWidth()
                    || canvas.getHeight() < source.getHeight()) {
                // 太小了就不画。现在不会更小。
                return true;
            }
            baseMatrix.setTranslate(
                    canvas.getWidth() - source.getWidth(), 0);
            canvas.drawBitmap(source, baseMatrix, null);
            return true;
        } else {
            return false;
        }
    }


    /**
     * 取编辑过的图
     * @return boolean is success
     */
    private static boolean drawProcessPicModelToBmp (
//            int color,
            Canvas canvas,
            ProcessPicModel model,
            Path path,
            RectF rectF) {
        boolean result = false;
        // 这里使用原图进行处理，不然因为用户可以放大导致生成图片失真
        long start = System.currentTimeMillis();

        // 用户编辑的缩放；
        float userEditScale = model.mPosScaModel.scale;

        // 乘以  一个范围，不用那么大的图
        int targetW = (int) (rectF.width() * userEditScale * 0.8f);
        int targetH = (int) (rectF.height() * userEditScale * 0.8f);

        Bitmap source = null;
        // 如果需要的图并不是那么大，那就讲究使用显示时加载的图了
        if (targetW <= ComposeUtil.getComposeWidth()
                && targetH <= ComposeUtil.getComposeWidth()) {
            source = BitmapCacheUtil.getValidBmpFromCache(model.getProcessedPath());
        }
        if (source == null || source.isRecycled()) {
            // nothing
            source = NightQImageLoader.loadBmpFromUriForBigPicture(
                    NightQAppLib.getAppContext(),
//                Integer.MAX_VALUE, Integer.MAX_VALUE,
                    targetW,
                    targetH,
                    model.getProcessedPath());
        }
        if (source != null && !source.isRecycled()) {
            // 0.用户旋转和翻转
            source = model.mPosScaModel.getProcessBmpWithTurnAndRotate(source);
        }
        if (source != null && !source.isRecycled()) {
            LogUtils.e("nightq",  " 合成时加载图 = "
                    + source.getWidth() + " * " + source.getHeight()
                    + " 时间 = " + (System.currentTimeMillis() - start)
                    + " 缩放 = " + userEditScale);
            start = System.currentTimeMillis();
            // 先保存画布
            canvas.save();
            // 裁剪出对应的位置
            canvas.clipPath(path);
            // create paint
            // 获取图片转换到对应的位置，所有的图片的编辑有两道过程，1.centercrop 和 2.用户编辑
            Matrix baseMatrix = new Matrix();
            Matrix supportMatrix = new Matrix();
            Matrix pathMatrix = new Matrix();
            Matrix resultMatrix = new Matrix();
            // 获取center crop 的model，为了先缩放到centercrop 然后缩放到对应位置
            PositionScaleModel positionScaleModel =
                    getCenterCropModel(rectF.width(),
                            rectF.height(), source.getWidth(), source.getHeight());
            // 1.center crop 转换
            baseMatrix.setScale(
                    positionScaleModel.scale,
                    positionScaleModel.scale);
            // 这里应该是center crop 的移动到居中位置。
            baseMatrix.postTranslate(
                    positionScaleModel.getTransformX(rectF.width()),
                    positionScaleModel.getTransformY(rectF.height()));

            // 2.用户编辑的缩放转换
            supportMatrix.setScale(
                    model.mPosScaModel.scale,
                    model.mPosScaModel.scale,
                    rectF.width()/2, rectF.height()/2);

            supportMatrix.postTranslate(
                    model.mPosScaModel.getTransformX(rectF.width()),
                    model.mPosScaModel.getTransformY(rectF.height()));

            // 3.移动到这个path 的位置
            pathMatrix.setTranslate(rectF.left, rectF.top);

            resultMatrix.set(baseMatrix);
            resultMatrix.postConcat(supportMatrix);
            resultMatrix.postConcat(pathMatrix);

            // 4.画背景图上去
            canvas.drawBitmap(source, resultMatrix, null);

            result = true;
            // 6. 恢复画布
            canvas.restore();
//            LogUtils.e("nightq",  " 合成时渲染时间 = " + (System.currentTimeMillis() - start));
        } else {
//            if (BuildConfig.DEBUG) {
//                canvas.drawColor(color);
//            }
        }
        return result;
    }
}
