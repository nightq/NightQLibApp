package freedom.nightq.puzzlepicture;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.DragEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

import de.greenrobot.event.EventBus;
import freedom.nightq.baselibrary.NightQAppLib;
import freedom.nightq.baselibrary.os.BaseActivity;
import freedom.nightq.baselibrary.threadPool.NormalEngine;
import freedom.nightq.baselibrary.utils.ExifUtils;
import freedom.nightq.baselibrary.utils.FileUtils;
import freedom.nightq.baselibrary.utils.LogUtils;
import freedom.nightq.baselibrary.utils.RecyclerViewUtil;
import freedom.nightq.baselibrary.utils.ResourceUtils;
import freedom.nightq.baselibrary.utils.ToastUtils;
import freedom.nightq.baselibrary.utils.imageLoader.NightQImageLoader;
import freedom.nightq.puzzlepicture.adapter.ComposeAdapter;
import freedom.nightq.puzzlepicture.enums.ComposeType;
import freedom.nightq.puzzlepicture.model.ComposeModel;
import freedom.nightq.puzzlepicture.model.ProcessComposeModel;
import freedom.nightq.puzzlepicture.model.ProcessPicModel;
import freedom.nightq.puzzlepicture.model.SelectedPhotosEvent;
import freedom.nightq.puzzlepicture.model.ToProcessPicEvent;
import freedom.nightq.puzzlepicture.utils.ComposeModelsUtils;
import freedom.nightq.puzzlepicture.utils.ComposeUtil;
import freedom.nightq.puzzlepicture.utils.Constants;
import freedom.nightq.puzzlepicture.widgets.ComposeLayout;
import freedom.nightq.puzzlepicture.widgets.DragableImageView;
import freedom.nightq.puzzlepicture.widgets.PolygonImageView;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * 选择版式
 */
@EActivity(resName="process_pics_compose_activity")
public class ProcessPicsComposeActivity extends BaseActivity
        implements
        ComposeAdapter.OnComposeChangelistener,
        PhotoViewAttacher.OnPhotoTapListener,
        View.OnDragListener, ComposeLayout.OnLoadFinishListener {

    /**
     * action : edit 编辑版式，在美化图片页面对版式进行编辑后返回到美化界面
     *          default choose 选择版式 一开始选图后就选版式然后美化
     */
    @Extra
    public String action;

    @ViewById(resName="composeLayout")
    ComposeLayout composeLayout;
    @ViewById(resName="recyclerView")
    RecyclerView recyclerView;
    RelativeLayout guide;
    LinearLayout guide1;
    RelativeLayout guide2;
    int guidePage = 0;

    @ViewById(resName="post_IV1")
    DragableImageView postIV1;
    @ViewById(resName="post_del1")
    ImageView postDel1;

    @ViewById(resName="post_IV2")
    DragableImageView postIV2;
    @ViewById(resName="post_del2")
    ImageView postDel2;

    @ViewById(resName="post_IV3")
    DragableImageView postIV3;
    @ViewById(resName="post_del3")
    ImageView postDel3;

    @ViewById(resName="post_IV4")
    DragableImageView postIV4;
    @ViewById(resName="post_del4")
    ImageView postDel4;

    DragableImageView[] imageViewList;
    ImageView[] imageViewDelList;

    /**
     * 全部数据
     */
    ProcessComposeModel mProcessComposeModel;

    ComposeAdapter mComposeAdapter;

    // 当前有几张图
    ComposeType mConposeType = ComposeType.ComposeTwoPic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 图片占内存，先回收一下
        Glide.get(this).clearMemory();
        System.gc();

        mComposeAdapter = new ComposeAdapter();
        mComposeAdapter.setOnComposeChangelistener(this);

        // getdata
        if (isFromProcessPics() || true) {
            // 美化图片进来的
            getDataFromProcessPics();
        } else {
            // 选图进来
            getDataFromEvent(false);
        }
        if (mProcessComposeModel == null) {
            if (isFromProcessPics()) {
                setResult(RESULT_CANCELED);
            }
            finish();
            return;
        }
    }

    /**
     * 是否是来自于图片美化界面
     * @return
     */
    public boolean isFromProcessPics () {
        return ProcessPicsActivity.class.getName().equalsIgnoreCase(action);
    }

    @AfterViews
    void initView() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //todo
                clickDone(0);
            }
        });

        imageViewList = new DragableImageView[]{postIV1, postIV2, postIV3, postIV4};
        imageViewDelList = new ImageView[]{postDel1, postDel2, postDel3, postDel4};

        // 设置 版式 占满屏 正方形
        LinearLayout.LayoutParams ll = (LinearLayout.LayoutParams) composeLayout.getLayoutParams();
        ll.width = ll.height = ComposeUtil.getComposeWidth();
        composeLayout.setLayoutParams(ll);

        // 设置actionbar
        int appMainColor = ResourceUtils.getColorResource(R.color.app_main_color);

        // 设置底部控制栏
        LinearLayoutManager layoutManagerForNormal = new LinearLayoutManager(this);
        layoutManagerForNormal.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(layoutManagerForNormal);
        recyclerView.setHasFixedSize(true);

        // 设置版式点击
        composeLayout.setOnPhotoTapListener(this);

        // 显示
        refreshPhoto();

        postIV1.setOnDragListener(this);
        postIV2.setOnDragListener(this);
        postIV3.setOnDragListener(this);
        postIV4.setOnDragListener(this);

        composeLayout.setOnLoadFinishListener(this);

    }

    @Override
    public void onLoadFinish(boolean isSuccess) {
        if (!isSuccess) {
            ToastUtils.showToast(R.string.loadError);
        }
    }

    @Override
    public boolean onDrag(View v, DragEvent event) {
        // Defines a variable to store the action type for the incoming event
        final int action = event.getAction();
        // Handles each of the expected events
        if (!(v instanceof DragableImageView)
                || event.getLocalState() == null
                || !(event.getLocalState() instanceof DragableImageView)) {
            return true;
        }
        DragableImageView from = (DragableImageView) event.getLocalState();
        DragableImageView to = (DragableImageView) v;

        int fromPosition = (Integer) from.getTag(R.id.item_view_tag_position);
        int toPosition = (Integer) to.getTag(R.id.item_view_tag_position);

        if (!from.isCanDragable()
                || !to.isCanDragable()
                || fromPosition < 0
                || toPosition < 0) {
            return true;
        }

        switch (action) {
            case DragEvent.ACTION_DRAG_STARTED:
                // 原图置空
                dragStarted(from);
                break;
            case DragEvent.ACTION_DRAG_ENTERED:
                to.setColorFilter(getResources().getColor(R.color.bg_black_30));
                to.invalidate();
                break;
            case DragEvent.ACTION_DRAG_LOCATION:
                break;
            case DragEvent.ACTION_DRAG_EXITED:
                to.clearColorFilter();
                to.invalidate();
                break;
            case DragEvent.ACTION_DROP:
                if (toPosition != fromPosition) {
                    // 交换位置
                    mProcessComposeModel.swapItem(fromPosition, toPosition);
                    mProcessComposeModel.mPicList.get(fromPosition).mPosScaModel.clearTransAndScale();
                    mProcessComposeModel.mPicList.get(toPosition).mPosScaModel.clearTransAndScale();
                    composeLayout.refreshComposeModel();
                    composeLayout.showPictures(false, new int[]{fromPosition, toPosition});
                }
                break;
            case DragEvent.ACTION_DRAG_ENDED:
                refreshThumbList();
                break;
            default:
                break;
        }
        return true;
    }

    /**
     * 开始拖动
     * @param from
     */
    public void dragStarted (DragableImageView from) {
        from.setAlpha(0.5f);
        postDel1.setVisibility(View.GONE);
        postDel2.setVisibility(View.GONE);
        postDel3.setVisibility(View.GONE);
        postDel4.setVisibility(View.GONE);
    }

    /**
     * 刷新图片显示列表
     * 完全重新加载，会重新选版式和缩放
     */
    private void refreshPhoto() {
        if (mProcessComposeModel == null) {
            return;
        }
        // 1.判断 几个图片
        mConposeType = ComposeModelsUtils.getComposeTypeFromCount(mProcessComposeModel.mPicList.size());
        // 2.设置版式到 layout
        if (composeLayout.setProcessComposeModel(
                mConposeType, mProcessComposeModel)) {
//            showDataLoadProgressDialog();
        } else {
            onLoadFinish(false);
        }
        // 设置版式选择列表
        mComposeAdapter.reset(mProcessComposeModel.mComposeModel.getDrawableId());
        // 刷新图片显示
        refreshThumbList();
        // 刷新 版式选择列表
        mComposeAdapter.setItems(ComposeModelsUtils
                .getComposeModelsByType(this, mConposeType));

//        long start = System.currentTimeMillis();
        if (recyclerView.getAdapter() == mComposeAdapter) {
            mComposeAdapter.notifyDataSetChanged();
        } else {
            recyclerView.setAdapter(mComposeAdapter);
        }
        // 滚到对应地方
        recyclerView.scrollToPosition(
                mComposeAdapter.getPositionByComposeId(
                        mProcessComposeModel.mComposeModel.getDrawableId()));
//        LogUtils.e("nightq", " refreshPhoto mComposeAdapter = " + (System.currentTimeMillis() - start));
    }

    /**
     * 刷新 顶部的 缩略图栏
     */
    @UiThread
    public void refreshThumbList () {
        for (int i=0; i<imageViewList.length; i++) {
            // change 不要add button 了
            imageViewList[i].clearColorFilter();
            imageViewList[i].setAlpha(1f);
            if (i>mProcessComposeModel.mPicList.size() && i > 0) {
                // 超过data 的view 隐藏
                imageViewList[i].setVisibility(View.GONE);
                imageViewList[i].setCanDragable(false);
                imageViewDelList[i].setVisibility(View.GONE);
            } else if (i==mProcessComposeModel.mPicList.size()) {
                // 图片后面一个是用来添加图片的按钮
                imageViewList[i].setBackgroundResource(R.color.transparent);
                imageViewList[i].setImageResource(R.drawable.image_add_photo);
                // for add photo －1 设置特别的 tag －1来标志为添加图片
                imageViewList[i].setTag(R.id.item_view_tag_position, -1);

                imageViewList[i].setVisibility(View.VISIBLE);
                imageViewList[i].setCanDragable(false);
                imageViewDelList[i].setVisibility(View.GONE);
            } else {
                imageViewList[i].setBackgroundResource(R.color.txt_hintGray);
                imageViewList[i].setCanDragable(true);
                showImageFromData(
                        i, mProcessComposeModel.mPicList.get(i),
                        imageViewList[i],
                        imageViewDelList[i], false);
            }
        }
    }


    @Override
    public void onPhotoTap(View view, float x, float y) {
        if (BuildConfig.DEBUG) {
//            clickPictureView(view);
        }
    }

    @Override
    public boolean onComposeChange(int position, ComposeModel model) {
        // 点击之后要一定 滚到对应位置
        RecyclerViewUtil.scrollToPositionForClick(recyclerView, position);
        // 修改数据
        mProcessComposeModel.setComposeModel(position, model);
        // 通过数据刷新界面
        composeLayout.refreshComposeModel();
        return true;
    }

    /**
     * 显示图片，并设置数据和 位置到tag 中，为了点击处理
     * @param position
     * @param bean
     * @param imageView
     */
    private void showImageFromData (
            int position,
            ProcessPicModel bean,
            ImageView imageView,
            ImageView imageViewDel,
            boolean isBigSize) {

        if (imageView != null) {
            imageView.setVisibility(View.VISIBLE);
            imageView.setTag(R.id.item_view_tag_position, position);
        }

        if (imageViewDel != null) {
            imageViewDel.setVisibility(View.VISIBLE);
            imageViewDel.setTag(R.id.item_view_tag, bean);
        }

        NightQImageLoader.displayPhotoWithUrl(
                isBigSize ? bean.getCutedPath()
                        : bean.getCutedPath(),
                imageView);
    }


    @Override
    protected void onPause() {
        if (composeLayout != null
                && composeLayout.imageViews != null) {
            // 保存每个位置
            for (PolygonImageView imageView : composeLayout.imageViews) {
                imageView.saveToPicModel();
            }
        }
        super.onPause();
    }

    long sureDeleteOnepic = 0;
    /**
     * 点击删除按钮
     * @param view
     */
    @Click(resName={"post_del1", "post_del2", "post_del3", "post_del4"})
    void clickPictureDelView(View view) {
        if  (mProcessComposeModel == null
                || mProcessComposeModel.mPicList == null
                || mProcessComposeModel.mPicList.size() <= 2) {
//            if (System.currentTimeMillis() - sureDeleteOnepic < 2000) {
            deletePic(view);
            if (mProcessComposeModel.mPicList.size() == 1) {
                gotoPicProcess(0);
            }
            finish();
            return;
//            } else {
//                ToastUtils.show(R.string.compose_two_pics_warn);
//                return;
//            }
        }
        deletePic(view);
        refreshPhoto();
    }

    /**
     * 删除一张图片
     * @param view
     */
    private void deletePic (View view) {
        if (mProcessComposeModel != null
                && mProcessComposeModel.mPicList != null
                && view.getTag(R.id.item_view_tag) != null) {
            mProcessComposeModel.mPicList.remove(view.getTag(R.id.item_view_tag));
            swapComposeAdapterIfNeed();
        }
        mProcessComposeModel.clearCompose();
        mProcessComposeModel.initDefaultCompose();
    }
    /**
     * 点击图片按钮
     * 可能是编辑或者新增图片
     * @param view
     */
    @Click(resName={"post_IV1", "post_IV2", "post_IV3", "post_IV4"})
    void clickPictureView(View view) {
        final int position = (Integer) view.getTag(R.id.item_view_tag_position);
        if (position == -1) {
            //add photo
            addPhoto();
        } else {
            if (BuildConfig.DEBUG) {
                clickDone(position);
            }
        }
    }

    /**
     * 跳到图片处理界面
     * @param position
     */
    public void gotoPicProcess (int position) {
        if (mProcessComposeModel == null) {
            return;
        }
        if (composeLayout == null
                || !composeLayout.isLoadedSucceed()) {
            ToastUtils.showToast(R.string.loadError);
            return;
        }
        // process picture  跳转到图片处理的界面
        EventBus.getDefault().postSticky(
                new ToProcessPicEvent(mProcessComposeModel, position));
        Intent intent = new Intent(this, ProcessPicsActivity_.class);
        intent.putExtra("hasAnim", false);
        startActivityForResult(intent, Constants.ACTIVITY_PUBLISH);
    }
    /**
     * 添加图，并附上已选的图
     */
    private void addPhoto() {
        if (mProcessComposeModel == null) {
            return;
        }
        try {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(intent, Constants.ACTIVITY_ADD_DATA);
        } catch (Exception ex) {
            finish();
        }

        // 版式选图的时候不用显示已选得了，因为可以选相同的
//        EventBus.getDefault().postSticky(getEventFromData());
//        Intent intent = new Intent(this, LocalSelectPhotoActivity_.class);
//        intent.putExtra("showCameraItem", true);
//        intent.putExtra("maxSelected",
//                Constants.FF_POST_MAX_COUNT - mProcessComposeModel.mPicList.size());
//        intent.putExtra(Constants.FROM_WHERE, PostWhiteActivity.class.getName());
//        startActivityForResult(intent, Constants.ACTIVITY_ADD_DATA);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == Constants.ACTIVITY_ADD_DATA) {// 添加图片
//            if(resultCode == RESULT_OK) {
//                getDataFromEvent(true);
//                showDataLoadProgressDialog();
//                refreshPhoto();
//            }
            if(resultCode != RESULT_OK) {
                return;
            }
            NormalEngine.getInstance().submit(new Runnable() {
                @Override
                public void run() {
                    Uri selectedImageURI = data.getData();
                    String getContentPath = FileUtils.getRealPathFromURI(selectedImageURI);
                    if (!TextUtils.isEmpty(getContentPath)) {
                        final LinkedHashMap<String, String> selectedBeans = new LinkedHashMap<String, String>();
                        selectedBeans.put(getContentPath, getContentPath);
                        NightQAppLib.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                SelectedPhotosEvent selectedPhotosEvent = new SelectedPhotosEvent(selectedBeans);
                                EventBus.getDefault().postSticky(selectedPhotosEvent);
                                getDataFromEvent(true);
                                refreshPhoto();
                            }
                        });
                    }
                }
            });
        } else if (requestCode == Constants.ACTIVITY_PUBLISH) {
            if (resultCode == RESULT_OK) {
                finish();
            } else {
                refreshPhoto();
            }
        }
    }

    /**
     * 从美化过来的取数据
     */
    public void getDataFromProcessPics () {
        ToProcessPicEvent contentBean = EventBus.getDefault().getStickyEvent(ToProcessPicEvent.class);
        if(contentBean != null) {
            mProcessComposeModel = contentBean.mData;
            contentBean.clear();
            EventBus.getDefault().removeStickyEvent(ToProcessPicEvent.class);
        }
    }

    /**
     * 从选图event  获取 list
     */
    public void getDataFromEvent (boolean isAdd) {
        SelectedPhotosEvent event = EventBus.getDefault().getStickyEvent(SelectedPhotosEvent.class);
        if (event != null && event.selectedBeans != null
                && event.selectedBeans.size() > 0) {
            if (mProcessComposeModel == null) {
                mProcessComposeModel = new ProcessComposeModel(event.selectedBeans, false);
            } else {
                if (!isAdd) {
                    mProcessComposeModel.mPicList.clear();
                }
                mProcessComposeModel.addPics(event.selectedBeans);
            }
            // 先换顺序，再初始化
            swapComposeAdapterIfNeed();
            mProcessComposeModel.initDefaultCompose();
        }
        EventBus.getDefault().removeStickyEvent(SelectedPhotosEvent.class);
    }

    /**
     * 如果需要就换版式列表顺序
     */
    public void swapComposeAdapterIfNeed () {
        if (mProcessComposeModel == null
                || mProcessComposeModel.mPicList == null
                || mProcessComposeModel.mPicList.size() != 3) {
            return;
        }
        try {
            long start = System.currentTimeMillis();
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            String localPath = mProcessComposeModel.mPicList.get(0).getOriginPath();
            BitmapFactory.decodeFile(localPath, options);

            int defaultOrientation = ExifUtils.getExifOrientation(localPath);
            // 只能换3个图的版式顺序
            List<ComposeModel> list = ComposeModelsUtils
                    .getComposeModelsByType(
                            NightQAppLib.getAppContext(),
                            ComposeModelsUtils.getComposeTypeFromCount(3));
            if (list == null || list.size() <= 0) {
                return;
            }
            boolean curIsH = "compose_3_4_1h".equalsIgnoreCase(list.get(0).drawable_name);
            boolean picIsH = options.outHeight < options.outWidth;
            if (defaultOrientation != 0
                    && defaultOrientation != ExifInterface.ORIENTATION_NORMAL
                    && defaultOrientation != ExifInterface.ORIENTATION_ROTATE_180) {
                picIsH = !picIsH;
            }
            boolean needSwap = (curIsH^picIsH);
            if (needSwap) {
                Collections.swap(list, 0, 1);
            }
            LogUtils.e("nightq", "swapComposeAdapterIfNeed time = " + (System.currentTimeMillis() - start));
        } catch (Exception e) {}

    }



    /**
     * 点击完成进去编辑
     * @param position
     */
    public void clickDone (int position) {
        if (isFromProcessPics()) {
            // 来自图片美化
            EventBus.getDefault().postSticky(
                    new ToProcessPicEvent(
                            mProcessComposeModel, position));
            setResult(RESULT_OK);
            finish();
        } else {
            gotoPicProcess(position);
        }
    }

    @Override
    public void onBackPressed() {
        if (isFromProcessPics()) {
            // 来自图片美化 需要 RESULT_CANCELED
            setResult(RESULT_CANCELED);
            super.onBackPressed();
        } else {
            ProcessPicsComposeActivity.super.onBackPressed();
        }
    }
}