package freedom.nightq.widgets.tagview.bean;

import java.io.Serializable;
import java.util.Random;

import freedom.nightq.widgets.tagview.util.ToolUtil;

/**
 * Created by Nightq on 16/3/2.
 */
public class TagInfoBean implements
        TagInfoBeanImpl, Serializable {

    /**
     * 增加一个 角度的字段，标志方向
     */
    public int angle = 0;

    public float positionX;
    public float positionY;

    /**
     * 左右翻转
     */
    @Override
    public void flipDirection () {
        switch (ToolUtil.unionAngle(angle)%360) {
            //                           left,           top,           right,          bottom
            case ToolUtil.Angle_45:
                angle = ToolUtil.Angle_135;
                break;
            case ToolUtil.Angle_135:
                angle = ToolUtil.Angle_45;
                break;
            case ToolUtil.Angle_180:
                angle = ToolUtil.Angle_0;
                break;
            case ToolUtil.Angle_M_135:
                angle = ToolUtil.Angle_M_45;
                break;
            case ToolUtil.Angle_M_45:
                angle = ToolUtil.Angle_M_135;
                break;
            case ToolUtil.Angle_0:
            default:
                angle = ToolUtil.Angle_180;
                break;
        }
    }

    @Override
    public String getDisplayContent() {
        return "angle" + angle;
    }

    @Override
    public int getAngle() {
        return angle;
    }

    @Override
    public Object getTag() {
        return null;
    }

    @Override
    public float getPositionX() {
        return positionX;
    }

    @Override
    public float getPositionY() {
        return positionY;
    }

    @Override
    public void setPositionX(float positionX) {
        this.positionX = positionX;
    }

    @Override
    public void setPositionY(float positionY) {
        this.positionY = positionY;
    }

    @Override
    public boolean isShowable() {
        return true;
    }
}