package freedom.nightq.widgets.tagview.bean;

import android.widget.RelativeLayout;

/**
 * Created by Nightq on 16/1/4.
 */
public class TagViewLayoutParamResult {

    public RelativeLayout.LayoutParams layoutParams;
    public int gravity;

    public TagViewLayoutParamResult(RelativeLayout.LayoutParams layoutParams, int gravity) {
        this.layoutParams = layoutParams;
        this.gravity = gravity;
    }
}
