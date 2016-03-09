package freedom.nightq.baselibrary.widgets;

import android.app.Activity;

import freedom.nightq.baselibrary.R;

/**
 * Created by Nightq on 14/10/23.
 */
public class WaitingViewController {

    private SimpleWaitingCircleView simpleWaitingCircleView;
    private Activity context;

    public int DefaultLayoutId = R.layout.dialog_simple_circle_view;

    public WaitingViewController (Activity context) {
        this.context = context;
    }

    private SimpleWaitingCircleView getSimpleWaitingCircleView () {
        if (simpleWaitingCircleView == null) {
            try {
                simpleWaitingCircleView = new SimpleWaitingCircleView(context, DefaultLayoutId);
            } catch (Exception e) {
                simpleWaitingCircleView = null;
            }
        }
        return simpleWaitingCircleView;
    }


    public boolean show () {
        return (getSimpleWaitingCircleView() != null && simpleWaitingCircleView.show());
    }

    public void hide () {
        if (simpleWaitingCircleView != null) {
            simpleWaitingCircleView.hide();
        }
    }

    public void onDestroy () {
        if (simpleWaitingCircleView != null) {
            simpleWaitingCircleView.onDestroy();
        }
    }

}
