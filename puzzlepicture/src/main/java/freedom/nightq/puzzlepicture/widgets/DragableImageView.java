package freedom.nightq.puzzlepicture.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

import freedom.nightq.puzzlepicture.model.CustomDragShadowBuilder;

/**
 * Created by Nightq on 15/12/28.
 */
public class DragableImageView extends ImageView {

    private boolean canDragable = true;

    public DragableImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public boolean isCanDragable() {
        return canDragable;
    }

    public void setCanDragable(boolean canDragable) {
        this.canDragable = canDragable;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (canDragable
                && event.getAction() == MotionEvent.ACTION_DOWN) {
            startDrag(null,
                    new CustomDragShadowBuilder(this),
                    this,
                    0);
            return true;
        }
        return super.onTouchEvent(event);
    }
}
