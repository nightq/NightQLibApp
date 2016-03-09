package freedom.nightq.puzzlepicture;

import android.support.v4.view.ViewPager;
import android.util.Log;

import com.bumptech.glide.Glide;

import de.greenrobot.event.EventBus;
import freedom.nightq.baselibrary.threadPool.NormalEngine;
import freedom.nightq.baselibrary.utils.DeviceUtils;
import freedom.nightq.baselibrary.utils.FileUtils;
import freedom.nightq.baselibrary.utils.LogUtils;
import freedom.nightq.baselibrary.utils.ResourceUtils;
import freedom.nightq.baselibrary.utils.StorageUtils;
import freedom.nightq.baselibrary.utils.ToastUtils;
import freedom.nightq.baselibrary.widgets.PointIndicatorView;
import freedom.nightq.baselibrary.widgets.PressTextView;
import freedom.nightq.baselibrary.widgets.SwipeViewPager;
import freedom.nightq.puzzlepicture.adapter.ProcessPicturesPagerAdapter;
import freedom.nightq.puzzlepicture.fragment.ProcessPictureFragment;
import freedom.nightq.puzzlepicture.model.ProcessComposeModel;
import freedom.nightq.puzzlepicture.model.ToProcessPicEvent;
import freedom.nightq.puzzlepicture.utils.BitmapCacheUtil;
import freedom.nightq.puzzlepicture.utils.ComposeUtil;

/**
 * Created by Nightq on 15/12/9.
 */
public class ProcessPicActivityHelper implements
        ViewPager.OnPageChangeListener {
    // 保存滤镜结果的图片，是一个临时文件，当用户正式上传完成之后把这个文件复制到照相机文件夹里即可
    // 主要为了避免用户多次修改滤镜，导致产生多次结果图片
    public static final String TMP_RESULT_PHOTO_PATH = StorageUtils.getAppTmpCacheDir() + "/tmpFilterResultPhoto";
    // context
    ProcessPicsActivity mProcessPicsActivity;

    // view for mul
    PressTextView imgNextForMul;
    PointIndicatorView pointIndicatorView;

    // for all
    SwipeViewPager viewPager;

    // data
    /**
     * 要编辑的图片
     */
    public ProcessComposeModel mData;
    // 初始化的默认位置
    public int defaultPosition;

    // adapter
    public ProcessPicturesPagerAdapter mAdapter;

    // view 是否在滚动
    public boolean isViewPagerScrolling = false;

    /**
     *
     * @param processPicsActivity
     */
    public ProcessPicActivityHelper(ProcessPicsActivity processPicsActivity) {
        mProcessPicsActivity = processPicsActivity;
        onCreate();
    }

    /**
     * on activity create
     */
    public void onCreate () {
        getDataFromEvent();
        if(mData == null
                || mData.mPicList == null
                || mData.mPicList.size() == 0) {
            mProcessPicsActivity.onBackPressed();
            return;
        }
    }

    /**
     * 从event 取data
     */
    public void getDataFromEvent () {
        ToProcessPicEvent contentBean = EventBus.getDefault().getStickyEvent(ToProcessPicEvent.class);
        if(contentBean != null) {
            if (contentBean.position >= 0) {
                defaultPosition = contentBean.position;
            }
            mData = contentBean.mData;
            contentBean.clear();
            EventBus.getDefault().removeStickyEvent(ToProcessPicEvent.class);
        }
    }

    /**
     * on after view
     */
    public void initAfterView () {
        if(mData == null
                || mData.mPicList == null
                || mData.mPicList.size() == 0) {
            return;
        }
        // INIT VIEW
        imgNextForMul = mProcessPicsActivity.imgNextForMul;
        pointIndicatorView = mProcessPicsActivity.pointIndicatorView;

        viewPager = mProcessPicsActivity.viewPager;

        viewPager.setSwipeable(false);
        // load data
        loadData();
        // refresh actionbar
        refreshActionBar();
    }

    /**
     * 刷新actionbar
     */
    public void refreshActionBar () {
        if(mData == null || mData.mPicList.size() == 0) {
            return;
        }
        if (mData.mPicList.size() == 1) {
        } else {
            // init mul pic actionbar
            pointIndicatorView.initView(
                    DeviceUtils.dpToPx(3),
                    ResourceUtils.getColorResource(R.color.app_main_color_20),
                    ResourceUtils.getColorResource(R.color.app_main_color),
                    DeviceUtils.dpToPx(8),
                    mData.mPicList.size());
            refreshControlBtnForMul();
        }
    }

    /**
     * 在多个图片编辑的时候需要在第一个和最后一个图片的时候禁用 上一张或者下一张的 按钮
     */
    public void refreshControlBtnForMul () {
        if (mData == null
                || viewPager == null
                || mAdapter == null) {
            return;
        }


        // 最后一个图，禁 下一张 按钮
        if (mData.mPicList.size() - 1 == viewPager.getCurrentItem()) {
            imgNextForMul.setText(R.string.btn_done);
        } else {
            imgNextForMul.setText(R.string.process_pic_title_next);
        }
        pointIndicatorView.setSelected(viewPager.getCurrentItem());
    }

    /**
     * 加载数据
     */
    public void loadData () {
        mAdapter = new ProcessPicturesPagerAdapter(
                mProcessPicsActivity,
                mProcessPicsActivity.getSupportFragmentManager());
        viewPager.setAdapter(mAdapter);
        viewPager.setCurrentItem(defaultPosition);
        viewPager.addOnPageChangeListener(this);
    }

    /**
     * 刷新全部图片，版式变了
     */
    public void refreshProcessPicsData () {
        // 刷新adapter
        if (mAdapter != null && viewPager != null) {
            viewPager.setAdapter(mAdapter);
            onPageSelected(viewPager.getCurrentItem());
        }
        // refresh actionbar
        refreshActionBar();
    }
    /**
     * 获取图片数量
     * @return
     */
    public int getPictureCount() {
        return mData.mPicList.size();
    }

    /**
     * 切换页面
     * @param addNumber 变换位置的多少
     */
    public void switchViewPager (int addNumber) {
        if (viewPager == null
                || mAdapter == null) {
            return;
        }
        if (isViewPagerScrolling) {
            // 正在滑就滑完了滑
            return;
        }
        viewPager.setCurrentItem(viewPager.getCurrentItem() + addNumber);
        refreshControlBtnForMul();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        // nothing
    }

    @Override
    public void onPageSelected(int position) {
        refreshFragmentData ();
        refreshControlBtnForMul();
        Log.e("nighqt", "onPageSelected = " + position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        switch (state) {
            case ViewPager.SCROLL_STATE_IDLE:
                isViewPagerScrolling = false;
                break;
            case ViewPager.SCROLL_STATE_DRAGGING:
                isViewPagerScrolling = true;
                break;
            case ViewPager.SCROLL_STATE_SETTLING:
                break;
        }
    }

    /**
     * 回收和load 对应的fragment
     */
    public void refreshFragmentData () {
        ProcessPictureFragment fragment;
        if (viewPager == null || mAdapter == null) {
            return;
        }
        for (int i=0; i<mAdapter.getCount(); i++) {
            fragment = (ProcessPictureFragment) mAdapter.getItem(i);
            if (i != viewPager.getCurrentItem()) {
                fragment.recycle();
            } else {
                fragment.loadFilterAndPendantBmp();
            }
        }
        // 图片占内存，先回收一下
        Glide.get(mProcessPicsActivity).clearMemory();
        System.gc();
    }


    /**
     * get current fragment
     * @return
     */
    public ProcessPictureFragment getCurrentFragment () {
        if (mAdapter == null
                || viewPager == null) {
            return null;
        }
        return mAdapter.getFragmentByPosition(viewPager.getCurrentItem());
    }

    /**
     * 显示图片处理的加载框
     */
    public void showPhotoProcessLoading() {
        ProcessPictureFragment currentFragment = getCurrentFragment();
        if(currentFragment == null
                || currentFragment.mPicFragmentHelper == null
                || isViewPagerScrolling) return;
        currentFragment.mPicFragmentHelper.showLoading();
    }

    public void hidePhotoProcessLoading() {
        ProcessPictureFragment currentFragment = getCurrentFragment();
        if(currentFragment == null
                || currentFragment.mPicFragmentHelper == null
                || isViewPagerScrolling) return;
        currentFragment.mPicFragmentHelper.hideLoading();
    }


    /**
     * 发布。
     * 需要先保存
     */
    public void publishClick () {
        // 保存并跳转
        NormalEngine.getInstance().submit(new Runnable() {
            @Override
            public void run() {
                long start = System.currentTimeMillis();
                boolean result = false;
                try {
                    result = publishContent();
                } catch (Exception e) {}
                LogUtils.e("nightq", "publishContent time = " + (System.currentTimeMillis() - start));
                if (result) {
                    // nothing
                } else {
                    ToastUtils.showToast(R.string.compose_save_error);
                }

            }
        });
    }

    /**
     * 发送内容
     * @return
     */
    private boolean publishContent () {
        if (mData == null
                || mData.mPicList == null
                || mData.mPicList.size() == 0) {
            return false;
        } else {
            ToProcessPicEvent event;
            // 多图需要生成

            long start = System.currentTimeMillis();
            // 1.保存滤镜
            LogUtils.e("nightq", "saveProcessPicToFile time = " + (System.currentTimeMillis() - start));
            // 2.生成大图
            String path = ComposeUtil.generateComposePics(
                    mData,
                    ComposeUtil.TargetComposeWidth,
                    ComposeUtil.TargetComposeHeight,
                    TMP_RESULT_PHOTO_PATH);
            if (!FileUtils.isFileExist(path)) {
                return false;
            }

            return true;
        }
    }

    public void destory() {
        mProcessPicsActivity = null;
        BitmapCacheUtil.clearCache();
    }
}
