package freedom.nightq.baselibrary.widgets;

import android.content.Context;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.widget.ProgressBar;

import freedom.nightq.baselibrary.R;
import freedom.nightq.baselibrary.utils.ResourceUtils;


/**
 */
public class CustomProgressBar extends ProgressBar {

    public CustomProgressBar(Context context) {
        super(context);
        init();
    }

    public CustomProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void init() {
        setColor(ResourceUtils.getColorResource(R.color.app_main_color));
    }

    public void setColor(int color) {
        getIndeterminateDrawable().setColorFilter(color, PorterDuff.Mode.MULTIPLY);
    }
}
