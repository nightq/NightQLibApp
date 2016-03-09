package freedom.nightq.puzzlepicture.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import freedom.nightq.baselibrary.os.BaseV4Fragment;
import freedom.nightq.baselibrary.utils.DeviceUtils;
import freedom.nightq.baselibrary.utils.ResourceUtils;
import freedom.nightq.baselibrary.widgets.CustomProgressBar;
import freedom.nightq.puzzlepicture.ProcessPicsActivity;
import freedom.nightq.puzzlepicture.R;
import freedom.nightq.puzzlepicture.model.ProcessComposeModel;
import freedom.nightq.puzzlepicture.model.ProcessPicModel;
import freedom.nightq.puzzlepicture.widgets.PathCoverImageView;
import freedom.nightq.puzzlepicture.widgets.PolygonImageView;

/**
 * Created by Nightq on 15/12/9.
 * activity must implements DataSource
 */
@EFragment(resName="process_pic_item")
public class ProcessPictureFragment extends BaseV4Fragment {

    // position key
    public static final String POSITION = "position";

    // 专门显示挂件
    @ViewById
    public PathCoverImageView pathCoverImageView;
    // 专门显示原图片
    @ViewById
    public PolygonImageView imageView;
    // 处理进度
    @ViewById
    public CustomProgressBar pacProgressBar;
    @ViewById(resName="process_pic_border")
    public ImageView borderIV;

    /**
     * 位置
     */
    public int position;

    // helper
    public ProcessPicFragmentHelper mPicFragmentHelper;

    public static ProcessPictureFragment newInstance(int position) {
        ProcessPictureFragment fragment = new ProcessPictureFragment_();
        Bundle bundle  = new Bundle();
        bundle.putInt(POSITION, position);
        fragment.setArguments(bundle);
        return fragment;
    }

    public ProcessPictureFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (getArguments() != null && getArguments().containsKey(POSITION)) {
            position = getArguments().getInt(POSITION);
        } else if (savedInstanceState != null && savedInstanceState.containsKey(POSITION)) {
            position = savedInstanceState.getInt(POSITION);
        }
        mPicFragmentHelper = new ProcessPicFragmentHelper(this);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(POSITION, position);
    }

    @AfterViews
    public void initAfterView () {
        if (mPicFragmentHelper != null) {
            mPicFragmentHelper.initAfterView();
        }
        pacProgressBar.setColor(ResourceUtils.getColorResource(R.color.bg_white));

        if(DeviceUtils.isNote2()) {
            pathCoverImageView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
    }

    @Override
    public void onDestroy() {
        if (mPicFragmentHelper != null) {
            mPicFragmentHelper.destory();
        }
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadFilterAndPendantBmp();
    }

    @Override
    public void onPause() {
        super.onPause();
        // 回收
        recycle();
    }

    /**
     *
     */
    public interface DataSource {
        ProcessComposeModel getComposeModel();
        ProcessPicModel getDataByPosition(int position);
        int getDataCount();
        int getCurrentItem();
    }

    /**
     * load
     */
    public void loadFilterAndPendantBmp () {
        if (mPicFragmentHelper != null) {
            mPicFragmentHelper.loadFilterAndPendantBmp();
        }
    }

    /**
     * 回收
     */
    public void recycle () {
        if (mPicFragmentHelper != null) {
            mPicFragmentHelper.recycle();
        }
    }
}
