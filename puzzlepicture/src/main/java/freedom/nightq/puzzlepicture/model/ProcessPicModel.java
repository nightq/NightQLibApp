package freedom.nightq.puzzlepicture.model;

import android.text.TextUtils;

import freedom.nightq.baselibrary.utils.StringUtils;

/**
 * Created by Nightq on 15/12/9.
 */
public class ProcessPicModel {

    // 图片
    private String imagePath;

    // 位置
    public int position;

    // 缩放和transform, 是在 centercrop 之后的变化
    public PositionScaleModel mPosScaModel = new PositionScaleModel();

    // cut 路径
    private String cutedPath;

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getImagePath() {
        return imagePath;
    }
    /**
     * 裁剪图片
     * @param cutedPath
     */
    public void setCutedPath(String cutedPath) {
        this.cutedPath = cutedPath;
    }

    /**
     * 获取裁剪过的图片
     * @return
     */
    public String getCutedPath () {
        if (!TextUtils.isEmpty(cutedPath)) {
            return cutedPath;
        }
        return getOriginPath();
    }


    /**
     * 获取原始的图片
     * @return
     */
    public String getOriginPath () {
        return imagePath;
    }


    /**
     * 显示处理过的大图
     * 发布的时候也会调用这个
     * @return
     */
    public String getProcessedPath() {
        return getCutedPath();
    }



    /**
     * 获取裁剪过后的路径的md5，用来存滤镜
     * @return
     */
    public String getCutedPathMd5() {
        return StringUtils.generateMD5String(getCutedPath());
    }

}
