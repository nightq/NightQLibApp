package freedom.nightq.baselibrary.widgets;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.SparseBooleanArray;
import android.view.ViewGroup;

/**
 * 主要是为了不回收view
 * Created by Nightq on 14-9-23.
 * ViewPager适配器
 */
public abstract class LiveFragmentPagerAdapter extends FragmentPagerAdapter {

    /**
     * 存放是否要让对应位置的fragment一直live，不销毁的状态
     * true：表示要销毁
     * false：表示不销毁
     * 默认为全部false，也就是默认全部都不销毁
     */
    private SparseBooleanArray liveFragmentPosition;

    public LiveFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
        liveFragmentPosition = new SparseBooleanArray();
    }

    /**
     * 是否destroyItem 这个position的fragment
     * @param position
     * @param isLive
     */
    public void setUnLivePosition (int position, boolean isLive) {
        liveFragmentPosition.put(position, isLive);
    }

    /**
     * 默认设置为live，也就是不销毁
     * @param position
     */
    public void setUnLivePosition (int position) {
        setUnLivePosition(position, true);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        if (liveFragmentPosition.get(position)) {
            super.destroyItem(container, position, object);
        }
    }
}