package freedom.nightq.baselibrary.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 */
public class PressImageView extends ImageView {

    public PressImageView(Context context) {
        super(context);
    }

    public PressImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_DOWN){
            setAlpha(0.5f);
        } else if(event.getAction() == MotionEvent.ACTION_MOVE){
//            setAlpha(1f);
        } else { //if(event.getAction() == MotionEvent.ACTION_UP)
            setAlpha(1f);
        }

        super.onTouchEvent(event);
        ((ViewGroup) getParent()).onTouchEvent(event);
        return true;
    }
}
