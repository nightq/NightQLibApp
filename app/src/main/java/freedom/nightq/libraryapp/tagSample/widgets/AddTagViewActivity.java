package freedom.nightq.libraryapp.tagSample.widgets;

import android.content.Intent;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import freedom.nightq.baselibrary.widgets.PopupMenuIconItem;
import freedom.nightq.baselibrary.widgets.PopupMenuWithIcon;
import freedom.nightq.libraryapp.R;
import freedom.nightq.widgets.tagview.TagGroupDefaultView;
import freedom.nightq.widgets.tagview.TagGroupPointView;
import freedom.nightq.widgets.tagview.bean.TagInfoBean;
import freedom.nightq.widgets.tagview.bean.TagInfoBeanImpl;
import freedom.nightq.widgets.tagview.util.ToolUtil;

public class AddTagViewActivity extends AppCompatActivity
        implements View.OnTouchListener, PopupMenuWithIcon.OnItemClickListener {

    private ArrayList<TagGroupDefaultView> tagViews = new ArrayList<>();

    private PopupMenuWithIcon mDialog;

    private RelativeLayout rootRL;
    private TagGroupPointView pointView;
    public ImageView photoIV;

    private int pointX;
    private int pointY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tag_sample_activity_edit);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        rootRL = (RelativeLayout) findViewById(R.id.layoutTags);
        pointView = (TagGroupPointView) findViewById(R.id.tagPoint);
        photoIV = (ImageView) findViewById(R.id.imageView);
        photoIV.setOnTouchListener(this);

        int widthPixels = getSreenWidth();
        ViewGroup.LayoutParams lp = rootRL.getLayoutParams();
        lp.width = widthPixels;
        lp.height = widthPixels;
        rootRL.setLayoutParams(lp);

        Button fab = (Button) findViewById(R.id.button);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickDone();
            }
        });
    }

    public void showEditDialog (View view) {
        if (mDialog != null) {
            mDialog.dismiss();
        }
        List<PopupMenuIconItem> list = new ArrayList<PopupMenuIconItem>();
        Object tag = view.getTag(R.id.imageTag_Point);
        list.add(new PopupMenuIconItem(
                0, null, "overturnTag", 1, tag));
        list.add(new PopupMenuIconItem(
                0, null, "delete", 2, tag));
        mDialog = new PopupMenuWithIcon(this,
                list, this);
        mDialog.setScreenCenter(true);
        mDialog.show();
    }

    public int getSreenWidth () {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return metrics.widthPixels;
    }

    /**
     * 完成
     */
    public void clickDone() {
        if (tagViews.size() < 1) {
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                // 保存滤镜
                final List<TagInfoBean> list = new ArrayList<TagInfoBean>();
                for (TagGroupDefaultView tagView : tagViews) {
                    if (tagView.getData() != null) {
                        for (TagInfoBeanImpl impl : tagView.getData()) {
                            list.add((TagInfoBean) impl);
                        }
                    }
                }
//                GlobalData.globalBigCircleMediaBean = mediaBean;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent();
                        intent.putExtra("DATA", new TagsModel(list));
//                        intent.putExtra(Constants.KEY_TAG, resultPhotoPath);
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                });
            }
        }).start();
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
//            && tvTabTag.isSelected()
            if (tagViews != null
                    && tagViews.size() > 0) {
                for (TagGroupDefaultView tagView : tagViews) {
                    if (tagView.isInThisTagView(motionEvent) != null) {
                        return false;
                    }
                }
            }
            pointX = (int) motionEvent.getX();
            pointY = (int) motionEvent.getY();
            startToAddTag(pointX, pointY);
            return true;
        }
        return false;
    }

    /**
     * 添加tag
     *
     * @param positionX
     * @param positionY
     */
    private void startToAddTag(final int positionX, final int positionY) {
        // 画动画那个点
        RelativeLayout.LayoutParams pointViewParams = (RelativeLayout.LayoutParams) pointView.getLayoutParams();
        pointViewParams.leftMargin = positionX - ToolUtil.getDimen(getResources(), R.dimen.dimen_8dp);
        pointViewParams.topMargin = positionY - ToolUtil.getDimen(getResources(), R.dimen.dimen_8dp);
        pointView.setLayoutParams(pointViewParams);
        pointView.setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                addRandTag();
            }
        }, 1000);
    }

    /**
     * 增加tag
     */
    private void addRandTag() {
        int total = new Random().nextInt(ToolUtil.Angle_Array.length) + 1;
        List<TagInfoBeanImpl> newTagData = new ArrayList<>();
        TagInfoBean bean;

        float pointX = getCurrentPointX();
        float pointY = getCurrentPointY();
        for (int i = 0; i < total; i++) {
            bean = new TagInfoBean();
            bean.positionX = pointX;
            bean.positionY = pointY;
            bean.angle = ToolUtil.Angle_Array[new Random().nextInt(ToolUtil.Angle_Array.length)];
            newTagData.add(bean);
        }
        addTagByBigCircleTagInfoBean(newTagData);
    }

    View.OnClickListener onTagClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view.getTag(R.id.imageTag_Point) != null) {
                showEditDialog(view);
            }
        }
    };

    /**
     *
     * @param beans
     */
    private void addTagByBigCircleTagInfoBean(
            List<TagInfoBeanImpl> beans) {
        if (beans == null || beans.size() == 0) {
            return;
        }

        TagGroupDefaultView tagGroupView = new TagGroupDefaultView(this, null);
        tagGroupView.setDataToView(beans);
        tagGroupView.setDragable(true);
        tagGroupView.setOnTagClickListener(onTagClickListener);
        rootRL.addView(tagGroupView,
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        tagViews.add(tagGroupView);
    }



    @Override
    public void onItemClick(//View view,
                     int id,
                     Object objectTag) {
        if (objectTag != null
                && objectTag instanceof PointF
                && tagViews != null) {
            TagGroupDefaultView targetView = null;
            for (TagGroupDefaultView view : tagViews) {
                if (DistanceUtil.isSamePoint((PointF) objectTag, view.getPointf())) {
                    targetView = view;
                    break;
                }
            }
            if (targetView == null) {
                return;
            }
            switch (id) {
                case 1:// 翻转tag
                    targetView.flipAllTag();
                    break;
                case 2:// 删除tag
                    tagViews.remove(targetView);
                    rootRL.removeView(targetView);
                    break;
            }
        }
        mDialog.dismiss();
    }

    int defaultViewWidth;

    public int getViewWidth() {
        if (photoIV.getWidth() <= 0) {
            return (int) getResources().getDimension(R.dimen.dimen_10dp) * 20;
        } else {
            return photoIV.getWidth();
        }
    }

    /**
     * 当前点的百分比
     *
     * @return
     */
    public float getCurrentPointX() {
        return pointX / (float) getViewWidth();
    }

    public float getCurrentPointY() {
        return pointY / (float) getViewWidth();
    }
}
