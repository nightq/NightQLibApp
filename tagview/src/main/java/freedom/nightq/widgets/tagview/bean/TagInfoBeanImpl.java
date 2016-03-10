package freedom.nightq.widgets.tagview.bean;

/**
 * Created by Nightq on 16/3/2.
 */
public interface TagInfoBeanImpl {
    /**
     * 显示的内容
     * @return
     */
    public String getDisplayContent ();
    /**
     * 显示的角度
     * @return
     */
    public int getAngle ();

    /**
     * tag
     * @return Object
     */
    public Object getTag ();

    /**
     * is show
     * @return boolean
     */
    public boolean isShowable ();
    /**
     *
     * @return float
     */
    public float getPositionX ();
    /**
     *
     * @return float
     */
    public float getPositionY ();

    /**
     *
     */
    public void setPositionX (float positionX);
    /**
     *
     */
    public void setPositionY (float positionY);


    /**
     * 翻转
     */
    public void flipDirection ();
}
