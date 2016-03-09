package freedom.nightq.baselibrary.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by H3c on 8/4/15.
 */
public class PressTextView extends TextView {

    public PressTextView(Context context, AttributeSet attrs) {
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
