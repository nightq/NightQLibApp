package freedom.nightq.baselibrary.utils;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import freedom.nightq.baselibrary.NightQAppLib;

/**
 * Created by Nightq on 15/12/8.
 */
public class RecyclerViewUtil {


    /**
     * 在切换图片的时候是否需要动画
     */
    public static final boolean needAnim = false;

    /**
     * 跳转到对应的位置。
     * @param recyclerView
     * @param position
     */
    public static void scrollToPositionForSet(
            final RecyclerView recyclerView,
            final int position) {
        if (needAnim) {
            scrollToPositionForSetAnim(recyclerView, position);
        } else {
            scrollToPositionForSetNoAnim(recyclerView, position);
        }
    }

    /**
     * 跳转到对应的位置。
     * @param recyclerView
     * @param position
     */
    public static void scrollToPositionForSetAnim(
            final RecyclerView recyclerView,
            final int position) {
        if (recyclerView != null) {
            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
            int firstItem = linearLayoutManager.findFirstVisibleItemPosition();
            int lastItem = linearLayoutManager.findLastVisibleItemPosition();
            // 统一跳到 居中
            int targetOffset = 0;
            if (position >= lastItem + 3) {
                targetOffset = position - 1;
            } else if (position <= firstItem - 3) {
                targetOffset = position + 1;
            }
            if (targetOffset != 0) {
                recyclerView.scrollToPosition(targetOffset);
                NightQAppLib.getInstance().getHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        smoothScrollToPosition(recyclerView, position, true);
                    }
                });
            } else {
                smoothScrollToPosition(recyclerView, position, true);
            }
        }

    }


    /**
     * 跳转到对应的位置。
     * @param recyclerView
     * @param position
     */
    public static void scrollToPositionForSetNoAnim(
            final RecyclerView recyclerView,
            final int position) {
        if (recyclerView != null) {
            smoothScrollToPosition(recyclerView, position, false);
        }
    }


    /**
     * 跳转到对应的位置。点击换滤镜的时候
     * @param recyclerView
     * @param position
     */
    public static void scrollToPositionForClick(
            final RecyclerView recyclerView,
            final int position) {
        if (recyclerView != null) {
            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
//             屏幕最后完全可见
            int lastCompletelyVisibleItemPosition = linearLayoutManager.findLastCompletelyVisibleItemPosition();
//             先直接滚过去。然后划过去
            if (position >= lastCompletelyVisibleItemPosition && lastCompletelyVisibleItemPosition >= 0) {
                // 点击的位置是未完全显示的item 那就滑动一下。
                try {
                    int lastItem = linearLayoutManager.findLastVisibleItemPosition();
                    int lastOffset = DeviceUtils.screenWPixels - linearLayoutManager.findViewByPosition(lastItem).getRight();
                    if (lastItem > position) {
                        recyclerView.smoothScrollBy(-lastOffset, 0);
                    } else if (lastItem == position) {
                        recyclerView.smoothScrollBy(DeviceUtils.dpToPx(105)
                                - lastOffset, 0);
                    }
                } catch (Exception e) {

                }
            }
        }
    }


    /**
     * smooth scroll
     * @param recyclerView
     * @param position
     */
    private static void smoothScrollToPosition(
            RecyclerView recyclerView,
            int position, boolean showAnim) {
        try {
            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
            int firstItem = linearLayoutManager.findFirstVisibleItemPosition();
            int firstOffset = linearLayoutManager.findViewByPosition(firstItem).getLeft();
            int offset = DeviceUtils.dpToPx(105) * (position - firstItem) + firstOffset
                    - (DeviceUtils.screenWPixels - DeviceUtils.dpToPx(105)) / 2;
            if (showAnim) {
                recyclerView.smoothScrollBy(offset, 0);
            } else {
                recyclerView.scrollBy(offset, 0);
            }
        } catch (Exception e) {

        }
    }
}
