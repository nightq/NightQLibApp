package freedom.nightq.widgets.tagview;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import freedom.nightq.widgets.tagview.bean.TagInfoBeanImpl;
import freedom.nightq.widgets.tagview.util.ToolUtil;

/**
 * 每个tag group view 在一个方向的layout，一般结构是 （阿迪达斯 童装）或者 （1080 人民币）
 * warn 只能用在tag group view 中, tag view 的 imageTag 会包含 bean
 */
public class TagDirectionBaseView extends LinearLayout {

    /**
     * 最多有两个cache。不然删除
     */
    public static final int CACHE_TAG_VIEW_COUNT = 2;

    /**
     * 所在的角度 默认 0
     */
    public int angle;

    /**
     * 重力方向，目前只支持左右，高度始终居中，默认 right
     */
    public int gravity;

    /**
     * tag 的点的中心，占显示的 view 的百分比
     */
    private PointF pointf;

    /**
     * tag 数据
     */
    public List<TagInfoBeanImpl> tagsData;

    /**
     * key View (TagInfoBeanImpl view), value rect  数据
     * 存放每个tag 的rect
     */
    public HashMap<View, Rect> rectForData;

    /**
     * 创建
     * @param context
     * @param attrs
     * @return
     */
    public static TagDirectionBaseView createInstance (Context context, AttributeSet attrs) {
        return new TagDirectionBaseView(context, attrs);
    }

    public TagDirectionBaseView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(LinearLayout.HORIZONTAL);
        setDividerDrawable(getResources().getDrawable(R.drawable.divider_transparent_w_4dp));
        setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
    }

    /**
     * 设置数据
     * @param tagsData
     */
    public void setDataToView(
            List<TagInfoBeanImpl> tagsData,
            PointF pointf,
            int angle,
            int gravity) {
        this.pointf = pointf;
        this.angle = angle;
        this.gravity = gravity;
        refreshDataSource(tagsData);
        // 设置 gravity
        setGravity(gravity);
        // 刷新 view 显示
        refreshView();
    }

    /**
     * 设置数据
     */
    private void refreshDataSource(
            List<TagInfoBeanImpl> data) {
        if (data == null) {
            this.tagsData = new ArrayList<>();
        } else {
            this.tagsData = data;
        }
    }

    /**
     * 清除rect
     */
    private void clearRect () {
        if (rectForData == null) {
            rectForData = new HashMap<>();
        } else {
            rectForData.clear();
        }
    }

    /**
     * 是否是这组tag
     * @return 为空表示没点到，或者返回对应的 child view
     */
    public View isInThisTagView (MotionEvent motionEvent) {
        float x = motionEvent.getX();
        float y = motionEvent.getY();
        if (rectForData == null) {
            return null;
        }
        Rect rect;
        for (View view : rectForData.keySet()) {
            rect = rectForData.get(view);
            if (x >= rect.left - TagGroupBaseView.TOUCH_LR_PADDING
                    && x <= rect.right + TagGroupBaseView.TOUCH_LR_PADDING
                    && y >= rect.top && y <= rect.bottom) {
//                LogHelper.e("nightq",
//                        "rect.contains = " + rect.contains((int)x, (int)y)
//                                + " rect = " + new Gson().toJson(rect) + " x*y" + x + "*" + y);
                return view;
            }
//            if (rect.contains((int)x, (int)y)) {
//                return view;
//            }
        }
        return null;
    }

    public List<TagInfoBeanImpl> getData() {
        return tagsData;
    }


    /**
     * 任何位置变化或者数据变化都可以调用这个方法来刷新
     * 刷新tag 显示
     */
    public void refreshView() {
        int dataSize = tagsData == null ? 0 : tagsData.size();
        // 遍历数据和view 刷新
        TextView tagView;
        TagInfoBeanImpl bean;
        for (int i=0; i<Math.max(dataSize, getChildCount()); i++) {
            if (i < getChildCount()) {
                // 刷新已经存在的 view 的内容
                tagView = (TextView) getChildAt(i);
                if (i < dataSize) {
                    // setData
                    bean = tagsData.get(i);
                    refreshTagViewByTagInfoBeanImpl(
                            bean,
                            tagView,
                            View.VISIBLE);
                } else {
                    if (i > CACHE_TAG_VIEW_COUNT) {
                        // 超过的删除
                        removeView(tagView);
                        i--;
                    } else {
                        // set view gone
                        refreshTagViewByTagInfoBeanImpl(
                                null,
                                tagView,
                                View.GONE);
                    }
                }
            } else {
                // 数据增加了，需要增加view 去显示
                bean = tagsData.get(i);
                // create view
                tagView = createTagViewByTagInfoBeanImpl();
                // show data
                refreshTagViewByTagInfoBeanImpl(
                        bean,
                        tagView,
                        View.VISIBLE);
                // add view
                addView(tagView, new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
                        ToolUtil.getDimen(getResources(), R.dimen.dimen_24dp)));
            }
        }
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
        if (getWidth() == 0) {
            return;
        }
        // clear cache
        clearRect();
        Rect rect;
        // 遍历 tag view update rect
        View tagView;
        // get view count
        int count = getChildCount();
        // get parent layout rect
        Rect layoutRect;
        layoutRect = new Rect();
        getHitRect(layoutRect);
        // 遍历
        for (int i=0; i<count; i++) {
            tagView = getChildAt(i);
            if (tagView.getTag(R.id.imageTag) != null
                    && tagView.getTag(R.id.imageTag) instanceof TagInfoBeanImpl) {
                // 获取每个tag 的框存起来，点击的时候要做判断
                rect = new Rect();
                tagView.getHitRect(rect);
                rect.offset(layoutRect.left, layoutRect.top);
                rectForData.put(tagView, rect);
            }
        }
    }

    /**
     * 创建一个 tagview
     * @return
     */
    private TextView createTagViewByTagInfoBeanImpl () {
        TextView textView = new TextView(getContext(), null);
        textView.setGravity(gravity|Gravity.CENTER_VERTICAL);
        textView.setTextSize(12);
        textView.setPadding(0, 0, 0, ToolUtil.getDimen(getResources(), R.dimen.dimen_2dp));
        textView.setSingleLine();
        textView.setEllipsize(TextUtils.TruncateAt.MIDDLE);
        textView.setTextColor(Color.WHITE);
        textView.setShadowLayer(
                ToolUtil.getDimen(getResources(), R.dimen.dimen_2dp),
                0,
                ToolUtil.getDimen(getResources(), R.dimen.dimen_1dp),
                ToolUtil.getColor(getResources(), R.color.black_80));
        return textView;
    }

    /**
     * 刷新 tagview
     * @return
     */
    private void refreshTagViewByTagInfoBeanImpl (
            TagInfoBeanImpl bean,
            TextView tagView, int visible) {
        tagView.setTag(R.id.imageTag_Point, pointf);
        tagView.setGravity(gravity|Gravity.CENTER_VERTICAL);
        if (bean != null) {
            tagView.setText(bean.getDisplayContent());
        } else {
            tagView.setText(null);
        }
        tagView.setTag(R.id.imageTag, bean);
        tagView.setVisibility(visible);
    }
}
