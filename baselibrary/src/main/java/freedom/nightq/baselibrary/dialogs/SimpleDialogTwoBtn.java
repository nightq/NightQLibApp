package freedom.nightq.baselibrary.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.widget.TextView;

import freedom.nightq.baselibrary.R;
import freedom.nightq.baselibrary.utils.Constants;
import freedom.nightq.baselibrary.utils.ResourceUtils;

/**
 * 简单的两个按钮的 dialog
 */
public class SimpleDialogTwoBtn extends DialogForNightQ {

	/**
	 * 可以修改布局
	 */
	public int layoutResId = R.layout.dialog_simple_two_btn;

	private android.view.View.OnClickListener onClickListener;

	private TextView tvDlgMsg;
	private String msgContentText;


	public SimpleDialogTwoBtn(Context mContext,
			android.view.View.OnClickListener onClickListener) {
		super(mContext, Constants.DIALOG_THEME);
        setConfirmDismissDialog(true);
		this.onClickListener = onClickListener;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setFocusDismiss(false);
        setConfirmListener(onClickListener);
		this.setContentView(layoutResId);
		init();
	}

	private void init() {
		tvDlgMsg = (TextView) findViewById(R.id.tvDlgMsg);
        setMessage();
    }

    private void setMessage() {
        if (!TextUtils.isEmpty(msgContentText)) {
            tvDlgMsg.setText(Html.fromHtml(msgContentText));
        }
    }

    @Override
	protected void onStart() {
        setMessage();
		super.onStart();
	}

	public void setDefMsgContent(int msgContentId) {
        setDefMsgContent(ResourceUtils.getResource().getString(msgContentId));
	}

	public void setDefMsgContent(String msgContentText) {
		this.msgContentText = msgContentText;
	}
}
