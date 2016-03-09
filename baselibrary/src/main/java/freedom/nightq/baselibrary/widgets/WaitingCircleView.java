package freedom.nightq.baselibrary.widgets;

import android.app.Activity;
import android.graphics.PixelFormat;
import android.text.Html;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.TextView;

import freedom.nightq.baselibrary.NightQAppLib;
import freedom.nightq.baselibrary.R;

/**
 * 主要用于loading或者waiting的小圈圈
 * Created by Nightq on 14-8-14.
 */
public class WaitingCircleView extends FloatViewBase {

    private TextView tvMsg;
    private String mContent;

    public static int DafaultLayoutId = R.layout.dialog_process;

    public void setContent(String mContent) {
        this.mContent = mContent;
        if (tvMsg != null) {
            tvMsg.setText(Html.fromHtml(mContent));
        }
    }

    /**
     * make一个默认的toast
     * @return
     */
    public WaitingCircleView(Activity activity) throws Exception {
        super(NightQAppLib.getAppContext(), NightQAppLib.getHandler(), DafaultLayoutId);
        mLayoutParams = new WindowManager.LayoutParams();
        mLayoutParams.gravity = Gravity.CENTER;
        mLayoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mLayoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
        mLayoutParams.format = PixelFormat.TRANSLUCENT;
        mLayoutParams.windowAnimations = android.R.style.Animation_Dialog;
        mLayoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION;//TYPE_APPLICATION_ATTACHED_DIALOG TYPE_TOAST

        setLayoutParams(mLayoutParams);

        tvMsg = (TextView)mLayout.findViewById(R.id.tvMsg);

        addTargetView();
    }
}
