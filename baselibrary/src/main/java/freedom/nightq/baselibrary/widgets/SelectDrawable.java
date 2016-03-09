package freedom.nightq.baselibrary.widgets;

import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;

import freedom.nightq.baselibrary.utils.PhotoUtils;
import freedom.nightq.baselibrary.utils.ResourceUtils;


/**
 * Created by Nightq on 15/8/15.
 */
public class SelectDrawable extends StateListDrawable {
    public SelectDrawable() {
    }

    public StateListDrawable addStateDrawable(int[] state, int resId, Integer color) {
        Drawable drawable;
        if (color == null) {
            drawable = ResourceUtils.getResource().getDrawable(resId);
        } else {
            drawable = new BitmapDrawable(ResourceUtils.getResource(),
                    PhotoUtils.changeBitmapColor(resId, color));
        }
        addState(state, drawable);
        return this;
    }

    //所以不要把大范围放在前面了，如果sd.addState(new[]{},normal)放在第一个的话，就没有什么效果了
//    sd.addState(new int[]{android.R.attr.state_enabled, android.R.attr.state_focused}, focus);
//    sd.addState(new int[]{android.R.attr.state_pressed, android.R.attr.state_enabled}, pressed);
//    sd.addState(new int[]{android.R.attr.state_focused}, focus);
//    sd.addState(new int[]{android.R.attr.state_pressed}, pressed);
//    sd.addState(new int[]{android.R.attr.state_enabled}, normal);
//    sd.addState(new int[]{}, normal);

}
