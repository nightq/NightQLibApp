package freedom.nightq.puzzlepicture.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.util.SparseArrayCompat;

import freedom.nightq.puzzlepicture.ProcessPicsActivity;
import freedom.nightq.puzzlepicture.fragment.ProcessPictureFragment;
import freedom.nightq.puzzlepicture.fragment.ProcessPictureFragment_;


/**
 * Created by Nightq on 15/12/9.
 */
public class ProcessPicturesPagerAdapter extends FragmentPagerAdapter {

    public SparseArrayCompat<ProcessPictureFragment> fragmentArray = new SparseArrayCompat<>();
    public ProcessPicsActivity mProcessPicsActivity;

    public ProcessPicturesPagerAdapter(
            ProcessPicsActivity processPicsActivity,
            FragmentManager fragmentManager) {
        super(fragmentManager);
        mProcessPicsActivity = processPicsActivity;
    }

    @Override
    public Fragment getItem(int position) {
        if (fragmentArray.get(position) == null) {
            fragmentArray.put(position, ProcessPictureFragment_.newInstance(position));
        }
        return fragmentArray.get(position);
    }

    /**
     * 获取 fragment
     * @param position
     * @return
     */
    public ProcessPictureFragment getFragmentByPosition (int position) {
        return fragmentArray.get(position);
    }

    @Override
    public int getCount() {
        return mProcessPicsActivity != null
                && mProcessPicsActivity.mUIHelper != null ? mProcessPicsActivity.mUIHelper.getPictureCount() : 0;
    }
}
