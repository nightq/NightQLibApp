package freedom.nightq.widgets.tagview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import freedom.nightq.widgets.tagview.bean.TagInfoBeanImpl;
import freedom.nightq.widgets.tagview.bean.TagViewLayoutParamResult;
import freedom.nightq.widgets.tagview.util.ToolUtil;

/**
 * 每组相同点的tag 的集合
 */
public class TagGroupBaseView<T extends TagDirectionBaseView> extends RelativeLayout
        implements View.OnClickListener {

    // 每一行字的高度
    int dp24;
    int dp8;
    int dp12;
    int dp4;

    /**
     * 中心点
     */
    protected TagGroupPointView tagPoint;

    /**
     * 是否可以拖动
     */
    private boolean isDragable = false;

    /**
     * tag 源数据
     */
    public List<TagInfoBeanImpl> tagsData;

    /**
     * key angle, value data  数据
     * 在这个方法之后刷新
     * @see #refreshDataSource(List)
     * 从这个数据分析获取
     * @see #tagsData
     */
    public HashMap<Integer, List<TagInfoBeanImpl>> dataByAngle = new HashMap<>();

    /**
     * rect  数据
     * 存放中间点的rect
     * warn 在layout 之后通过 才获取到的
     */
    public Rect rectForPoint;

    /**
     * 每一个角度的所有 tag view 的 list
     */
    public List<TagDirectionBaseView> tagViewsList = new ArrayList<>();
    /**
     * tag 的点的中心，占显示的 view 的百分比
     * @warn 在layout 之后通过view 的宽高计算出来的
     */
    private PointF pointf = new PointF();

    /**
     * 点击事件
     */
    private OnClickListener mOnClickListener;
    Paint mPaint;

    public PointF getPointf() {
        return pointf;
    }

    /**
     * 创建
     * @param context
     * @param attrs
     * @return
     */
    public static TagGroupBaseView createInstance (Context context, AttributeSet attrs) {
        return new TagGroupBaseView(context, attrs);
    }

    public TagGroupBaseView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint();
        mPaint.setColor(ToolUtil.getColor(getResources(), R.color.white_50));
        mPaint.setAntiAlias(true);

        move_max = ToolUtil.getDimen(getResources(), R.dimen.dimen_10dp);
        dp24 = ToolUtil.getDimen(getResources(), R.dimen.dimen_24dp);
        dp12 = ToolUtil.getDimen(getResources(), R.dimen.dimen_12dp);
        dp4 = ToolUtil.getDimen(getResources(), R.dimen.dimen_4dp);
        dp8 = ToolUtil.getDimen(getResources(), R.dimen.dimen_8dp);

        TOUCH_LR_PADDING = ToolUtil.getDimen(getResources(), R.dimen.dimen_2dp);

        LayoutInflater.from(context).inflate(R.layout.layout_goods_tag_group, this, true);
        tagPoint = (TagGroupPointView) findViewById(R.id.tagPoint);

        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                refreshView();
                getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });
    }

    /**
     * 设置ta改是否可以额被drag
     * @param dragable
     */
    public void setDragable(boolean dragable) {
        isDragable = dragable;
    }

    public void setOnTagClickListener(OnClickListener mOnClickListener) {
        this.mOnClickListener = mOnClickListener;
    }

    /**
     * 设置数据
     * @param tagsData
     */
    public void setDataToView(List<TagInfoBeanImpl> tagsData) {
        refreshDataSource(tagsData);
        refreshView();
    }

    /**
     * 设置数据，纯粹的对新数据进行解析，按角度分组。
     * 并且把缓存的数据刷新
     */
    private void refreshDataSource(
            List<TagInfoBeanImpl> data) {
        if (data == null) {
            this.tagsData = new ArrayList<>();
        } else {
            this.tagsData = data;
        }

        dataByAngle.clear();
        rectForPoint = null;

        int angle;
        List someAngleList;
        TagInfoBeanImpl bean;
        for (int i=0; i<tagsData.size(); i++) {
            bean = tagsData.get(i);
            // 初始化位置和角度
            pointf.x = bean.getPositionX();
            pointf.y = bean.getPositionY();
            angle = bean.getAngle();
            someAngleList = dataByAngle.get(angle);
            if (someAngleList == null) {
                someAngleList = new ArrayList();
                dataByAngle.put(angle, someAngleList);
            }
            someAngleList.add(bean);
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        if (getViewWidth() == 0) {
            super.dispatchDraw(canvas);
            return;
        }
        if (dataByAngle != null && dataByAngle.size() > 0) {
            int pointX = getPointLeftMargin();
            int pointY = getPointTopMargin();
            for (int angle : dataByAngle.keySet()) {
                switch (ToolUtil.unionAngle(angle)%360) {
                    //                           left,           top,           right,          bottom
                    case ToolUtil.Angle_45:
                        drawLine(canvas,
                                pointX, pointY,
                                pointX + dp12, pointY + dp12);
                        drawLine(canvas,
                                pointX + dp12, pointY + dp12,
                                pointX + dp12 + dp12, pointY + dp12);
                        break;
                    case ToolUtil.Angle_135:
                        drawLine(canvas,
                                pointX, pointY,
                                pointX - dp12, pointY + dp12);
                        drawLine(canvas,
                                pointX - dp12, pointY + dp12,
                                pointX - dp12 - dp12, pointY + dp12);
                        break;
                    case ToolUtil.Angle_180:
                        // nothing
                        break;
                    case ToolUtil.Angle_M_135:
                        drawLine(canvas,
                                pointX, pointY,
                                pointX - dp12, pointY - dp12);
                        drawLine(canvas,
                                pointX - dp12, pointY - dp12,
                                pointX - dp12 - dp12, pointY - dp12);
                        break;
                    case ToolUtil.Angle_M_45:
                        drawLine(canvas,
                                pointX, pointY,
                                pointX + dp12, pointY - dp12);
                        drawLine(canvas,
                                pointX + dp12, pointY - dp12,
                                pointX + dp12 + dp12, pointY - dp12);
                        break;
                    case ToolUtil.Angle_0:
                    default:
                        // nothing
                        break;
                }
            }
        }
        super.dispatchDraw(canvas);
    }

    /**
     * 划线
     * @param canvas
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     */
    public void drawLine (Canvas canvas, float x1, float y1, float x2, float y2) {
        mPaint.setStrokeWidth(3);
        mPaint.setColor(ToolUtil.getColor(getResources(), R.color.white_50));
//        mPaint.setShadowLayer(Global.dpToPx(2), 0, Global.dpToPx(1), Global.getColor(R.color.black_80));
        // Important for certain APIs
//        setLayerType(LAYER_TYPE_SOFTWARE, null);
        canvas.drawLine(
                x1, y1,
                x2, y2,
                mPaint);
        mPaint.setStrokeWidth(1);
        mPaint.setColor(ToolUtil.getColor(getResources(), R.color.black_50));
        canvas.drawLine(
                x1, y1 + 2,
                x2, y2 + 2,
                mPaint);
        mPaint.setColor(ToolUtil.getColor(getResources(), R.color.black_10));
        canvas.drawLine(
                x1, y1 + 3,
                x2, y2 + 3,
                mPaint);
//        setLayerType(LAYER_TYPE_HARDWARE, null);
    }

    private boolean inThisViewWhenDown = false;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        // todo nightq now 是否拦截
        int action = motionEvent.getAction();
        if (action == MotionEvent.ACTION_DOWN) {
            downX = motionEvent.getRawX();
            downY = motionEvent.getRawY();
            lastX = motionEvent.getRawX();
            lastY = motionEvent.getRawY();
            if (isInThisTagView (motionEvent) != null) {
                inThisViewWhenDown = true;
            } else {
                inThisViewWhenDown= false;
            }
            return inThisViewWhenDown;
        }
        if (action == MotionEvent.ACTION_MOVE) {
            if (inThisViewWhenDown) {
                return true;
            }
        }
        return super.onInterceptTouchEvent(motionEvent);
    }

    /**
     * touch 的时候 padding
     */
    public static int TOUCH_LR_PADDING = 10;
    /**
     * 是否是这组tag
     * @return 为空表示没点到，或者返回对应的
     */
    public View isInThisTagView (MotionEvent motionEvent) {
        float x = motionEvent.getX();
        float y = motionEvent.getY();
        if (getViewWidth() == 0) {
            return null;
        }
        if (rectForPoint != null
                && isInThisTagView(tagPoint, rectForPoint, x, y) != null) {
            return tagPoint;
        }
        TagDirectionBaseView tagView;
        for (int i=0; i<getChildCount(); i++) {
            if (getChildAt(i) instanceof TagDirectionBaseView
                    && getChildAt(i).getVisibility() == View.VISIBLE) {
                tagView = (TagDirectionBaseView) getChildAt(i);
                View view = tagView.isInThisTagView(motionEvent);
                if (view != null) {
                    return view;
                }
            }
        }
        return null;
    }

    public static View isInThisTagView (View view, Rect rect, float x, float y) {
        if (x >= rect.left - TOUCH_LR_PADDING && x <= rect.right + TOUCH_LR_PADDING
                && y >= rect.top && y <= rect.bottom) {
//                LogHelper.e("nightq",
//                        "rect.contains = " + rect.contains((int)x, (int)y)
//                                + " rect = " + new Gson().toJson(rect) + " x*y" + x + "*" + y);
            return view;
        }
//            if (rect.contains((int)x, (int)y)) {
//                return view;
//            }
        return null;
    }


    private static int move_max = 10;
    // 按下的坐标
    private float lastX = 0;
    private float lastY = 0;
    private float downX = 0;
    private float downY = 0;
    private long lastMoveTime = 0;

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        int action = motionEvent.getAction();
        float offsetX = (motionEvent.getRawX() - lastX);
        float offsetY = (motionEvent.getRawY() - lastY);
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                //nothing
                return inThisViewWhenDown;
            case MotionEvent.ACTION_MOVE:
                if (Float.compare(offsetX, 10) > 0
                        || Float.compare(offsetY, 10) > 0
                        || System.currentTimeMillis() - lastMoveTime > 50) {
                    // nothing
                } else {
                    return inThisViewWhenDown;
                }
            case MotionEvent.ACTION_UP:
                if (action == MotionEvent.ACTION_MOVE) {
                    tagPoint.pauseAnim();
                } else if (action == MotionEvent.ACTION_UP) {
                    tagPoint.resumeAnim();
                }
                if (isDragable && moveTag(offsetX, offsetY)) {
                    lastX = motionEvent.getRawX();
                    lastY = motionEvent.getRawY();
                    lastMoveTime = System.currentTimeMillis();
                    if (action == MotionEvent.ACTION_UP) {
                        saveTagPosition();
                    }
                }
                // 距离太多就是move
                if (Double.compare(
                        distance(
                                motionEvent.getRawX(), motionEvent.getRawY(), downX, downY), move_max) > 0
                        || Double.compare(
                        distance(
                                motionEvent.getRawX(), motionEvent.getRawY(), lastX, lastY), move_max) > 0) {
                    // nothing
                } else if (action == MotionEvent.ACTION_UP) {
                    View clickView = isInThisTagView(motionEvent);
                    if (clickView != null) {
                        onClick(clickView);
                    }
                }
        }
        return inThisViewWhenDown;
    }

    /**
     * 亮点距离
     * @return
     */
    public static double distance(
            float x1, float y1, float x2, float y2){
        return Math.sqrt(Math.pow(x1 - x2, 2) +
                Math.pow(y1-y2, 2));
    }

    private static float POSITION_MIN = 0.01F;
    private static float POSITION_MAX = 0.99F;

    /**
     * 移动 tag
     *  moveTag(motionEvent.getRawX(), motionEvent.getRawY());
     * @param offsetX
     * @param offsetY
     */
    private boolean moveTag(float offsetX, float offsetY) {
        if (getViewWidth() == 0) {
            return false;
        }
        pointf.x = pointf.x + offsetX/getViewWidth();
        pointf.y = pointf.y + offsetY/getViewHeight();
        if (pointf.x < POSITION_MIN) {
            pointf.x = POSITION_MIN;
        } else if (pointf.x > POSITION_MAX) {
            pointf.x = POSITION_MAX;
        }
        if (pointf.y < POSITION_MIN) {
            pointf.y = POSITION_MIN;
        } else if (pointf.y > POSITION_MAX) {
            pointf.y = POSITION_MAX;
        }
        refreshView();
        return true;
    }

    /**
     * 保存移动的结果
     */
    private void saveTagPosition () {
        if (tagsData != null) {
            for (TagInfoBeanImpl bean : tagsData) {
                bean.setPositionX(pointf.x);
                bean.setPositionY(pointf.y);
            }
        }
    }


    public List<TagInfoBeanImpl> getData() {
        return tagsData;
    }


    /**
     * 获取某一个角度上的第一个 tag 的 LayoutParams
     * @return
     */
    private TagViewLayoutParamResult getFirstTagParamsForSomeAngle(
            int angle) {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

        // tag 点中心
        int baseLeft = getPointLeftMargin();
        int baseTop = getPointTopMargin();
        int baseRight = getPointRigthMargin();

        int[] paramsMargin;
        switch (ToolUtil.unionAngle(angle)%360) {
            //                           left,           top,           right,          bottom
            case ToolUtil.Angle_45:
                paramsMargin = new int[]{dp24 + dp4 + baseLeft, baseTop, 0, 0, Gravity.LEFT};
                break;
            case ToolUtil.Angle_135:
                paramsMargin = new int[]{0, baseTop, baseRight+(dp24 + dp4), 0, Gravity.RIGHT};
                break;
            case ToolUtil.Angle_180:
                paramsMargin = new int[]{0, baseTop-(dp24 / 2), baseRight+dp12, 0, Gravity.RIGHT};
                break;
            case ToolUtil.Angle_M_135:
                paramsMargin = new int[]{0, baseTop-dp24, baseRight+(dp24 + dp4), 0, Gravity.RIGHT};
                break;
            case ToolUtil.Angle_M_45:
                paramsMargin = new int[]{dp24 + dp4 + baseLeft, baseTop-dp24, 0, 0, Gravity.LEFT};
                break;
            case ToolUtil.Angle_0:
            default:
                paramsMargin = new int[]{dp12 + baseLeft, baseTop-(dp24 / 2), 0, 0, Gravity.LEFT};
                break;
        }
        params.setMargins(
                paramsMargin[0],
                paramsMargin[1],
                paramsMargin[2],
                paramsMargin[3]);
        if (paramsMargin[4] == Gravity.LEFT) {
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        } else {
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        }
//        LogHelper.e("nightq", " paramsMargin = " + new Gson().toJson(paramsMargin));
        return new TagViewLayoutParamResult(params, paramsMargin[4]);
    }

    public int getPointLeftMargin () {
        return (int) (pointf.x * getViewWidth());
    }
    public int getPointRigthMargin () {
        return (int) ((1-pointf.x) * getViewWidth());
    }
    public int getPointTopMargin () {
        return (int) (pointf.y * getViewHeight());
    }

    /**
     * 增加 tag views
     */
    private void refreshView() {
        if (getViewWidth() == 0) {
            // 还没layout 就不处理。
            return;
        }

        // 设置点的位置
        tagPoint.setVisibility(View.VISIBLE);
        refreshPointPosition();

        // 设置数据到view 中
        List<TagInfoBeanImpl> beans;
        // 获取 angle
        List<Integer> angleList = new ArrayList<>(dataByAngle.keySet());
        int angle;

        // 显示tag
        TagDirectionBaseView childTagsView;

        // 补全 每个angle 的 BigCircleTagDirectionView
        // 并且显示
        for (int i=0; i<Math.max(tagViewsList.size(), angleList.size()); i++) {
            if (i<tagViewsList.size()) {
                childTagsView = tagViewsList.get(i);
                if (i<angleList.size()) {
                    angle = angleList.get(i);
                    beans = dataByAngle.get(angle);
                    if (beans != null && beans.size() > 0) {
                        childTagsView.setVisibility(View.VISIBLE);
                        showDirectionTagView(childTagsView, beans, angle);
                    } else {
                        childTagsView.setVisibility(View.GONE);
                    }
                } else {
                    childTagsView.setVisibility(View.GONE);
                }
            } else {
                // need create view
                childTagsView = T.createInstance(getContext(), null);
                tagViewsList.add(childTagsView);
                addView(childTagsView);
                // get data
                angle = angleList.get(i);
                beans = dataByAngle.get(angle);
                // show data
                showDirectionTagView(childTagsView, beans, angle);
            }
        }
    }

    /**
     * 显示单个view，并且设置位置
     */
    private void showDirectionTagView (
            TagDirectionBaseView childTagsView,
            List<TagInfoBeanImpl> beans,
            int angle) {
        // 获取显示参数
        TagViewLayoutParamResult result = getFirstTagParamsForSomeAngle(angle);
        childTagsView.setDataToView(beans, pointf, angle, result.gravity);
        childTagsView.setLayoutParams(result.layoutParams);
    }


    /**
     * 刷新 tag 点的位置
     */
    private void refreshPointPosition () {
        RelativeLayout.LayoutParams pointLayoutParams
                = (RelativeLayout.LayoutParams) tagPoint.getLayoutParams();
        pointLayoutParams.setMargins(
                getPointLeftMargin() - dp8,
                getPointTopMargin() - dp8,0,0);
        tagPoint.setLayoutParams(pointLayoutParams);
        // tag
        tagPoint.setTag(R.id.imageTag_Point, pointf);
    }


    /**
     * 翻转tag
     */
    public void flipAllTag () {
        if (tagsData == null) {
            return;
        }
        // 翻转数据
        for (TagInfoBeanImpl bean : tagsData) {
            bean.flipDirection();
        }
        // 刷新数据
        refreshDataSource(tagsData);
        // 刷新 view
        refreshView();
    }



    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        updateRect();
    }

    /**
     * 更新tag 区域
     */
    private void updateRect () {
        rectForPoint = null;
        if (getWidth() == 0
                || tagViewsList.size() == 0
                || tagPoint == null) {
            return;
        }
        // for point rect
        rectForPoint = new Rect();
        tagPoint.getHitRect(rectForPoint);
        int pointX = getPointLeftMargin();
        int pointY = getPointTopMargin();
        for (int angle : dataByAngle.keySet()) {
            switch (ToolUtil.unionAngle(angle)%360) {
                //                           left,           top,           right,          bottom
                case ToolUtil.Angle_45:
                    rectForPoint.right = Math.max(rectForPoint.right, pointX + dp24);
                    rectForPoint.bottom = Math.max(rectForPoint.bottom, pointY + dp24);
                    break;
                case ToolUtil.Angle_135:
                    rectForPoint.left = Math.min(rectForPoint.left, pointX - dp24);
                    rectForPoint.bottom = Math.max(rectForPoint.bottom, pointY + dp24);
                    break;
                case ToolUtil.Angle_180:
                    rectForPoint.left = Math.min(rectForPoint.left, pointX - dp8);
                    rectForPoint.top = Math.min(rectForPoint.top, pointY - dp8);
                    rectForPoint.bottom = Math.max(rectForPoint.bottom, pointY + dp8);
                    // nothing
                    break;
                case ToolUtil.Angle_M_135:
                    rectForPoint.left = Math.min(rectForPoint.left, pointX - dp24);
                    rectForPoint.top = Math.min(rectForPoint.top, pointY - dp24);
                    break;
                case ToolUtil.Angle_M_45:
                    rectForPoint.top = Math.min(rectForPoint.top, pointY - dp24);
                    rectForPoint.right = Math.max(rectForPoint.right, pointX + dp24);
                    break;
                case ToolUtil.Angle_0:
                default:
                    rectForPoint.top = Math.min(rectForPoint.top, pointY - dp8);
                    rectForPoint.right = Math.max(rectForPoint.right, pointX + dp8);
                    rectForPoint.bottom = Math.max(rectForPoint.bottom, pointY + dp8);
                    break;
            }
        }
    }

    @Override
    public void onClick(View view) {
        if (isDragable) {
            // 编辑界面认为都是编辑
            if (null != mOnClickListener) {
                mOnClickListener.onClick(tagPoint);
            }
        } else {
            if (view.getTag(R.id.imageTag) instanceof TagInfoBeanImpl) {
                if (null != mOnClickListener) {
                    mOnClickListener.onClick(view);
                }
            }
        }
    }

    public int getViewHeight () {
        return getHeight();
    }

    public int getViewWidth () {
        return getWidth();
    }
}
