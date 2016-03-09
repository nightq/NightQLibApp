package freedom.nightq.baselibrary.widgets;

import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.TextView;

import freedom.nightq.baselibrary.NightQAppLib;
import freedom.nightq.baselibrary.R;

/**
 * Created by Nightq on 14-8-14.
 */
public class ToastBase extends FloatViewBase {

    private TextView tvMessage;

    public static int DafaultLayoutId = R.layout.toast_for_nightq;

    /**
     * make一个默认的toast
     * @return
     */
    public ToastBase(int layoutId) throws Exception {
        super(NightQAppLib.getAppContext(), NightQAppLib.getHandler(),
                layoutId == 0 ? DafaultLayoutId : layoutId);
        mLayoutParams = new WindowManager.LayoutParams();
        mLayoutParams.gravity = Gravity.BOTTOM;
        mLayoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mLayoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
        mLayoutParams.format = PixelFormat.TRANSLUCENT;
        mLayoutParams.windowAnimations = android.R.style.Animation_Toast;
        mLayoutParams.type = WindowManager.LayoutParams.TYPE_TOAST;

        setLayoutParams(mLayoutParams);

        tvMessage = (TextView)mLayout.findViewById(R.id.message);

        addTargetView();
    }


    /**
     * 主要是用于默认的toast
     * @param s
     */
    public void setText(CharSequence s) {
        if (tvMessage != null) {
            tvMessage.setText(s);
        }
    }

}
