package freedom.nightq.widgets.tagview;

import android.content.Context;
import android.graphics.PointF;
import android.support.v4.util.SimpleArrayMap;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.List;

import freedom.nightq.widgets.tagview.bean.TagInfoBeanImpl;

/**
 * 将多个tagview封装到一起
 */
public class TagGroupBaseLayout<T extends TagGroupBaseView> extends FrameLayout implements View.OnClickListener {
    private static final int MAX_TAG_GROUP_COUNT = 5;
    private List<TagGroupBaseView> mTagGroups = new ArrayList<>();

    public TagGroupBaseLayout(Context context) {
        super(context);
    }

    public TagGroupBaseLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TagGroupBaseLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 点击事件
     */
    private OnClickListener mOnClickListener;

    public void setOnTagClickListener(OnClickListener mOnClickListener) {
        this.mOnClickListener = mOnClickListener;
    }

    public void setData(List<TagInfoBeanImpl> tags) {
        // 先给tag分组
        SimpleArrayMap<PointF, List<TagInfoBeanImpl>> tagGroupList = new SimpleArrayMap<>();
        for (TagInfoBeanImpl tag : tags) {
            if (!tag.isShowable()) {
                continue;//只显示in_photo的tag
            }
            PointF point = new PointF(tag.getPositionX(), tag.getPositionY());
            List<TagInfoBeanImpl> tagGroup;
            if (tagGroupList.containsKey(point)) {
                tagGroup = tagGroupList.get(point);
            } else {
                tagGroup = new ArrayList<>();
                tagGroupList.put(point, tagGroup);
            }
            tagGroup.add(tag);
        }
        // 看看tag够不够用
        int tagsSize = Math.min(tagGroupList.size(), MAX_TAG_GROUP_COUNT);
        while (mTagGroups.size() < tagsSize) {
            TagGroupBaseView view = T.createInstance(getContext(), null);
            addView(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            mTagGroups.add(view);
        }
        // 隐藏所有tag
        for (TagGroupBaseView view : mTagGroups) {
            view.setVisibility(INVISIBLE);
        }
        // 同组的放到一个BigCircleTagGroupView中
        for (int i = 0; i < tagsSize; i++) {
            TagGroupBaseView tagGroupView = mTagGroups.get(i);
            tagGroupView.setDataToView(tagGroupList.valueAt(i));
            tagGroupView.setOnTagClickListener(this);
            tagGroupView.setVisibility(VISIBLE);
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getTag(R.id.imageTag) instanceof TagInfoBeanImpl) {
            TagInfoBeanImpl bean = (TagInfoBeanImpl) view.getTag(R.id.imageTag);
            if (mOnClickListener != null) {
                mOnClickListener.onClick(view);
            }
        }
    }
}
