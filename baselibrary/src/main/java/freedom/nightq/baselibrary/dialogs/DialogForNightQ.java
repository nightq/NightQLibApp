package freedom.nightq.baselibrary.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import freedom.nightq.baselibrary.R;
import freedom.nightq.baselibrary.utils.DeviceUtils;
import freedom.nightq.baselibrary.utils.ResourceUtils;

/**
 * 一个通用的无按钮，一个按钮，两个按钮，的对话框。可以设置布局，但是注意布局的按钮一定要对应
 * 默认为两个button，且分别为cancel和confirm。
 * cancel为点击取消对话框，回调cancellistener。confirm会取消对话框，调用confirmlistener。
 * 默认为非全屏，点击空白区域会取消对话框，点击后退取消对话框。
 */
public class DialogForNightQ extends DialogBaseForNightQ {

    /**
     * 可以修改布局
     */
    public int layoutResId = R.layout.dialog_base_layout_for_nightq;

    /**
     * 可以设置删除按钮的颜色
     */
    public int deleteBtnColor = R.color.phone_plus_red;
    /**
     * 可以设置背景图片
     */
    public int backgroundPicture = R.drawable.transparent;

    enum DialogButtonNumber {
       NO_BUTTON, ONE_BUTTON, TWO_BUTTONS
    }


    private int dlgBackgroundPicture = -1;
	private boolean focusDismiss = true;
    private boolean haveTitle = false;
    private boolean isDeleteDialog = false;
    private boolean cancelDismissDialog = true;
    private boolean confirmDismissDialog = false;
    private int paddingForSide;
    private int contentMinHeight;
    private DialogButtonNumber buttonNumber = DialogButtonNumber.TWO_BUTTONS;
    private String title;
    private String tvCancelContent;
    private String tvConfirmContent;
    private String tvOneBtnContent;

    private View.OnClickListener cancelListener;
    private View.OnClickListener confirmListener;
    private View.OnClickListener btnOneListener;

    private View.OnClickListener commonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == tvCancel.getId()) {
                if (cancelListener != null) {
                    cancelListener.onClick(v);
                }
                if (cancelDismissDialog) {
                    dismiss();
                }
            } else if (v.getId() == tvConfirm.getId()) {
                if (confirmListener != null) {
                    confirmListener.onClick(v);
                }
                if (confirmDismissDialog) {
                    dismiss();
                }
            } else if (v.getId() == btnOne.getId()) {
                if (btnOneListener != null) {
                    btnOneListener.onClick(v);
                }
                if (cancelDismissDialog) {
                    dismiss();
                }
            }
        }
    };



    public void setConfirmDismissDialog(boolean confirmDismissDialog) {
        this.confirmDismissDialog = confirmDismissDialog;
    }


    public void setCancelDismissDialog(boolean cancelDismissDialog) {
        this.cancelDismissDialog = cancelDismissDialog;
    }


    public void setConfirmContent(String tvConfirmContent) {
        this.tvConfirmContent = tvConfirmContent;
    }

    public void setCancelContent(String tvCancelContent) {
        this.tvCancelContent = tvCancelContent;
    }

    public void setOneBtnContent(String tvOneBtnContent) {
        this.tvOneBtnContent = tvOneBtnContent;
    }

    public void setOnBtnTextStyle(int textStyle) {
        if(btnOne != null) {
            btnOne.setTextAppearance(getContext(), textStyle);
        }
    }

    public void setDlgBackgroundPicture(int dlgBackgroundPicture) {
        this.dlgBackgroundPicture = dlgBackgroundPicture;
    }

    public void setCancelListener(View.OnClickListener cancelListener) {
        this.cancelListener = cancelListener;
    }

    public void setConfirmListener(View.OnClickListener confirmListener) {
        this.confirmListener = confirmListener;
    }

    public void setOneListener(View.OnClickListener btnOneListener) {
        this.btnOneListener = btnOneListener;
    }


    public void setSingleButton() {
        this.buttonNumber = DialogButtonNumber.ONE_BUTTON;
    }

    public void setNoButton() {
        this.buttonNumber = DialogButtonNumber.NO_BUTTON;
    }

    /**
     * 设置button数目，默认两个按钮
     * @param buttonNumber
     */
    public void setDialogButtonNumber(DialogButtonNumber buttonNumber) {
        this.buttonNumber = buttonNumber;
    }


    /**
     * 设置是否有title
     */
    public void setHaveTitle(boolean haveTitle) {
        this.haveTitle = haveTitle;
    }

    /**
     * 设置title
     */
    public void setTitle(String title) {
        if (!TextUtils.isEmpty(title)) {
            this.title = title;
            this.haveTitle = true;
        } else {
            this.haveTitle = false;
        }
        setTitleView();
    }

    /**
     * 设置title
     */
    public void setTitle(int res) {
        if (res != 0) {
            setTitle(ResourceUtils.getResource().getString(res));
        } else {
            this.haveTitle = false;
        }
    }


    /**
     * 设置是否点击空白区域消失
     */
    public void setFocusDismiss(boolean focusDismiss) {
        setCanceledOnTouchOutside(focusDismiss);
        this.focusDismiss = focusDismiss;
    }

    public void setContentMinHeight(int contentMinHeight) {
        this.contentMinHeight = contentMinHeight;
    }

    protected DialogForNightQ(Context context, int theme) {
		super(context, theme);
        paddingForSide = DeviceUtils.dpToPx(40);
        contentMinHeight = DeviceUtils.dpToPx(100);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}


    /**
     * 设置对话框两边的宽度
     * @param padding
     */
    public void setPaddingContent (int padding) {
        paddingForSide = padding;
        View view = findViewById(R.id.activityContentLayout);
        if (view != null) {
            view.setPadding(paddingForSide, 0, paddingForSide, 0);
        }
    }

    public void setContentView(int resId) {
        super.setContentView(layoutResId);
        LayoutInflater.from(DialogForNightQ.this.getContext()).inflate(resId, (LinearLayout) findViewById(R.id.layouBaseDialogContent), true);
        init();
    }


    private LinearLayout layoutMainContent;
    private TextView tvCancel;
    private TextView tvConfirm;

    public TextView getTvConfirm() {
        return tvConfirm;
    }

    public TextView getTvCancel() {
        return tvCancel;
    }

    public TextView getBtnOne() {
        return btnOne;
    }

    private TextView tvTitle;
    private TextView btnOne;

    private void init() {
        if (focusDismiss) {
            findViewById(R.id.activityContentLayout).setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });
        }
        findViewById(R.id.layouBaseDialogContent).setMinimumHeight(contentMinHeight);

        layoutMainContent = (LinearLayout) findViewById(R.id.layoutBaseDialogMainContent);
        tvTitle = (TextView) findViewById(R.id.tvBaseDialogTitle);
        tvCancel = (TextView) findViewById(R.id.tvBaseDialogCancel);
        tvConfirm = (TextView) findViewById(R.id.tvBaseDialogConfirm);
        btnOne = (TextView) findViewById(R.id.btnBaseDialogOne);
        setTitleView();
        if (isDeleteDialog) {
            tvConfirm.setTextColor(ResourceUtils.getColorStateList(deleteBtnColor));
        }
        if (!TextUtils.isEmpty(tvConfirmContent)) {
            tvConfirm.setText(tvConfirmContent);
        }
        if (!TextUtils.isEmpty(tvCancelContent)) {
            tvCancel.setText(tvCancelContent);
        }
        if (!TextUtils.isEmpty(tvOneBtnContent)) {
            btnOne.setText(tvOneBtnContent);
        }
        btnOne.setOnClickListener(commonListener);
        tvCancel.setOnClickListener(commonListener);
        tvConfirm.setOnClickListener(commonListener);
        if (dlgBackgroundPicture > 0) {
            layoutMainContent.setBackgroundResource(dlgBackgroundPicture);
        }
        setPaddingContent(paddingForSide);
        showButton();
    }

    private void setTitleView() {
        if (tvTitle == null) {
            return;
        }
        if (haveTitle) {
            tvTitle.setVisibility(View.VISIBLE);
            tvTitle.setText(title);
        } else {
            tvTitle.setVisibility(View.GONE);
        }
    }

    /**
     * 显示按钮
     */
    public void showButton () {
        //还没有初始化的话就直接返回。
        if (layoutMainContent == null) {
            return;
        }
        switch (buttonNumber) {
            case NO_BUTTON:
                findViewById(R.id.layoutBaseDialogButton).setVisibility(View.GONE);
                findViewById(R.id.layoutBaseDialogTwoButton).setVisibility(View.GONE);
                findViewById(R.id.btnBaseDialogOne).setVisibility(View.GONE);
                break;
            case ONE_BUTTON:
                findViewById(R.id.layoutBaseDialogButton).setVisibility(View.VISIBLE);
                findViewById(R.id.layoutBaseDialogTwoButton).setVisibility(View.GONE);
                findViewById(R.id.btnBaseDialogOne).setVisibility(View.VISIBLE);
                break;
            case TWO_BUTTONS:
                findViewById(R.id.layoutBaseDialogButton).setVisibility(View.VISIBLE);
                findViewById(R.id.layoutBaseDialogTwoButton).setVisibility(View.VISIBLE);
                findViewById(R.id.btnBaseDialogOne).setVisibility(View.GONE);
                break;
        }
    }

    /**
     * 隐藏按钮
     */
    public void hideButton () {
        //还没有初始化的话就直接返回。
        if (layoutMainContent == null) {
            return;
        }
        findViewById(R.id.layoutBaseDialogButton).setVisibility(View.GONE);
    }

    public static int getBtnConfirmId () {
        return R.id.tvBaseDialogConfirm;
    }

    public static int getBtnCancelId () {
        return R.id.tvBaseDialogCancel;
    }

    public static int getBtnOneId () {
        return R.id.btnBaseDialogOne;
    }

    /**
     * 设置对话框为删除对话框，也就是确认的按钮为红色
     *
     */
    public void setDeleteDialog () {
        isDeleteDialog = true;
    }
    public static void setFocusDismiss (View view, final Dialog dlg) {
        if (view != null && dlg != null) {
            dlg.setCanceledOnTouchOutside(true);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dlg.dismiss();
                }
            });
        }
    }

    /**
     * 设置没有按钮没有title没有背景的居中的对话框
     */
    public void setTransparentDialog () {
        setNoButton();
        setHaveTitle(false);
        setDlgBackgroundPicture(backgroundPicture);
    }
}
