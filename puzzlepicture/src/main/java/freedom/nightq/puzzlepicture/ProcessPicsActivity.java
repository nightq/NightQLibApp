package freedom.nightq.puzzlepicture;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import de.greenrobot.event.EventBus;
import freedom.nightq.baselibrary.dialogs.SimpleDialogTwoBtn;
import freedom.nightq.baselibrary.os.BaseActivity;
import freedom.nightq.baselibrary.utils.FileUtils;
import freedom.nightq.baselibrary.utils.PhotoUtils;
import freedom.nightq.baselibrary.utils.ResourceUtils;
import freedom.nightq.baselibrary.utils.StringUtils;
import freedom.nightq.baselibrary.utils.ViewUtils;
import freedom.nightq.baselibrary.widgets.PointIndicatorView;
import freedom.nightq.baselibrary.widgets.PressImageView;
import freedom.nightq.baselibrary.widgets.PressTextView;
import freedom.nightq.baselibrary.widgets.SwipeViewPager;
import freedom.nightq.puzzlepicture.fragment.ProcessPictureFragment;
import freedom.nightq.puzzlepicture.model.ProcessComposeModel;
import freedom.nightq.puzzlepicture.model.ProcessPicModel;
import freedom.nightq.puzzlepicture.model.ToProcessPicEvent;
import freedom.nightq.puzzlepicture.utils.Constants;

/**
 */
@EActivity(resName="process_pics_activity")
public class ProcessPicsActivity extends BaseActivity
        implements ProcessPictureFragment.DataSource {

    @Extra
    boolean isPostFOF = true;

    @ViewById(resName="viewPager")
    SwipeViewPager viewPager;

    // for mul actionbar
    @ViewById(resName="imgBackForMul")
    PressImageView backBtn;
    @ViewById(resName="actionBarForMul")
    RelativeLayout actionBarForMul;
    @ViewById(resName="imgNextForMul")
    PressTextView imgNextForMul;
    @ViewById(resName="pointIndicatorView")
    PointIndicatorView pointIndicatorView;

    @Extra
    boolean hasAnim = true;

    public ProcessPicActivityHelper mUIHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //  图片占内存，先回收一下
        Glide.get(this).clearMemory();
        System.gc();

        mUIHelper = new ProcessPicActivityHelper(this);
    }

    @AfterViews
    void init() {
        mUIHelper.initAfterView();

        findViewById(R.id.btnSave).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //todo
                if (mUIHelper != null) {
                    mUIHelper.publishClick();
                }
            }
        });

        findViewById(R.id.btnEdit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mUIHelper != null) {
                    EventBus.getDefault().postSticky(
                            new ToProcessPicEvent(mUIHelper.mData, 0));
                    Intent intent = new Intent(ProcessPicsActivity.this, ProcessPicsComposeActivity_.class);
                    intent.putExtra("action", ProcessPicsActivity.class.getName());
                    // 选择版式
                    startActivityForResult(intent, Constants.ACTIVITY_MODIFY_TAG);
                }
            }
        });

        backBtn.setImageBitmap(PhotoUtils.changeBitmapColor(R.mipmap.btn_icon_back_normal,
                ResourceUtils.getColorResource(R.color.app_main_color)));
    }

    /**
     * 返回空
     * @param position
     * @return
     */
    @Override
    public ProcessPicModel getDataByPosition(int position) {
        if (mUIHelper != null && mUIHelper.mData != null
                && mUIHelper.mData.mPicList.size() > position
                && position >= 0) {
            return mUIHelper.mData.mPicList.get(position);
        }
        return null;
    }

    /**
     * 返回
     * @return
     */
    @Override
    public ProcessComposeModel getComposeModel () {
        return mUIHelper.mData;
    }


    /**
     * 返回
     * @return
     */
    @Override
    public int getDataCount () {
        return 0;
    }


    /**
     * 返回当前 position
     * @return
     */
    @Override
    public int getCurrentItem () {
        return viewPager.getCurrentItem();
    }

    @Click(resName={"imgBackForMul", "imgNextForMul", "imgLastForMul"})
    public void onClickControlForMul (View view) {
        int i = view.getId();
        if (i == R.id.imgBackForMul) {
            onBackPressed();
        } else if (i == R.id.imgNextForMul) {
            if (mUIHelper != null
                    && mUIHelper.mData != null
                    && mUIHelper.mData.mPicList.size() - 1
                    == viewPager.getCurrentItem()) {
                mUIHelper.publishClick();
            } else {
                mUIHelper.switchViewPager(1);
            }

        } else if (i == R.id.imgLastForMul) {
            mUIHelper.switchViewPager(-1);

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.ACTIVITY_MODIFY_TAG) {
            // refresh
            if (mUIHelper != null) {
                mUIHelper.getDataFromEvent();
                mUIHelper.refreshProcessPicsData();
            }
        } else if (requestCode == Constants.ACTIVITY_CUT_PIC) {
            if (resultCode == RESULT_OK
                    && data != null
                    && mUIHelper != null
                    && mUIHelper.mData != null
                    && viewPager != null) {
                String result = data.getStringExtra("resultPath");
                if (FileUtils.isFileExist(result)) {
                    mUIHelper.mData.mPicList.get(viewPager.getCurrentItem()).setCutedPath(result);
                    mUIHelper.refreshProcessPicsData();
                }
            }
        }
    }


    @Override
    protected void onDestroy() {
        if (mUIHelper != null) {
            mUIHelper.destory();
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    /**
     * 获取正在处理的图，用于漫画边框界面预览
     * @return
     */
    public Bitmap getCurrentFilterProcessingViewBmp() {
        ProcessPictureFragment fragment = mUIHelper.getCurrentFragment();
        if(fragment == null) return null;

        Drawable photoDrawable = fragment.imageView.getDrawable();
        if(photoDrawable != null) {
            return ((BitmapDrawable) photoDrawable).getBitmap();
        }

        return ViewUtils.createBitmapFromView(fragment.imageView);
    }
}