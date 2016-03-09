package freedom.nightq.baselibrary.widgets;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import freedom.nightq.baselibrary.R;
import freedom.nightq.baselibrary.dialogs.DialogBaseForNightQ;
import freedom.nightq.baselibrary.dialogs.DialogForNightQ;
import freedom.nightq.baselibrary.utils.DeviceUtils;
import freedom.nightq.baselibrary.utils.imageLoader.NightQImageLoader;

/**
 * Created by Nightq on 14-9-1.
 */
public class PopupMenuWithIcon extends DialogBaseForNightQ {

    /**
     * item 布局 id
     */
    public int itemLayoutId = R.layout.popup_menu_icon_item;
    /**
     * 整个布局的 id
     */
    public int layoutId = R.layout.dialog_popup;
    public int backBottomItemResId = R.drawable.dlg_share_bottom_item_bg;

    public int backBottomResId = R.drawable.dlg_share_bottom_bg;
    public int backCenterResId = R.drawable.dlg_share_center_bg;
    public int backTopResId = R.drawable.dlg_share_bg;
    public int backResId = R.color.dlg_background;
    public int popupMenuAnimId = R.style.popup_menu_animation;

    private Context context;
    private List<PopupMenuIconItem> listItems;
    private OnItemClickListener onItemClickListener;

    private LinearLayout layoutContent;
    private LinearLayout list;

    private LayoutInflater mInflater;

    private int top;
    private int right;

    /**
     * icon
     */
    private int iconSize;
    private int iconPreRes;

//    /**
//     * line height
//     */
//    private int lineHeight;

    public void setScreenBottom(boolean isScreenBottom) {
        this.isScreenBottom = isScreenBottom;
    }

    private boolean isScreenBottom;

    private boolean isScreenCenter;

    public void setScreenCenter(boolean isScreenCenter) {
        this.isScreenCenter = isScreenCenter;
    }

    /**
     * for lock view
     */
    public void setmView(View mView) {
        this.mView = mView;
    }


    /**
     * 设置icon大小
     */
    public void setIconSize(int max) {
        iconSize = max;
    }

    public void setIconPreRes(int iconPreRes) {
        this.iconPreRes = iconPreRes;
    }

//    public void setLineHeight(int lineHeight) {
//        this.lineHeight = lineHeight;
//    }

    /**
     * for lock view
     */
    private View mView;

    /**
     * 设置框的右上角位置
     * @param top
     * @param right
     */
    public void setPosition(int top, int right) {
        this.top = top;
        this.right = right;
    }

    /**
     * 创建PopupMenuWithIcon
     * @param context
     */
    public PopupMenuWithIcon(Context context, List<PopupMenuIconItem> listItems, OnItemClickListener onItemClickListener) {
        super(context, R.style.theme_dialog_transparent_3);
        this.listItems = listItems;
        this.context = context;
        this.onItemClickListener = onItemClickListener;
        mInflater = LayoutInflater.from(context);
        setAnimation(false);
        setWindowMatchParentHeight(ViewGroup.LayoutParams.MATCH_PARENT);
//        setWindowMatchParent(false);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layoutId);

        layoutContent = (LinearLayout) findViewById(R.id.layoutContent);
        list = (LinearLayout) findViewById(R.id.list);
        //设置点击layout就dismiss
        DialogForNightQ.setFocusDismiss(findViewById(R.id.layout), PopupMenuWithIcon.this);

        if(isScreenBottom) {
            findViewById(R.id.layout).setPadding(0, (top),
                    right - DeviceUtils.dpToPx(5 * 2 / 3), 0);
        } else if (isScreenCenter) {
            View layout = findViewById(R.id.layout);
            ViewGroup.LayoutParams layoutParams = layout.getLayoutParams();
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
            layout.setLayoutParams(layoutParams);

            RelativeLayout.LayoutParams contentParams = (RelativeLayout.LayoutParams) layoutContent.getLayoutParams();
            contentParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
            contentParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
            contentParams.addRule(RelativeLayout.CENTER_IN_PARENT);
            layoutContent.setLayoutParams(contentParams);
        } else {
            findViewById(R.id.layout).setPadding(0, top - DeviceUtils.dpToPx(5 * 2 / 3),
                    right - DeviceUtils.dpToPx(5 * 2 / 3), 0);
        }

        if(isScreenBottom) {
            layoutContent.setBackgroundResource(backBottomResId);
        } else if (isScreenCenter) {
            layoutContent.setBackgroundResource(backCenterResId);
            getWindow().setBackgroundDrawableResource(backResId);
        } else {
            layoutContent.setBackgroundResource(backTopResId);
        }
        getWindow().setWindowAnimations(popupMenuAnimId);
        // 对全部的item设置
        PopupMenuIconItem popupMenuIconItem;
        for (int i = 0; i < listItems.size(); i++) {
            popupMenuIconItem = listItems.get(i);
            addItemView(popupMenuIconItem);
        }
    }

    public void addItemView(PopupMenuIconItem popupMenuIconItem) {
        View convertView = mInflater.inflate(itemLayoutId, null);
        ImageView itemIcon = (ImageView) convertView.findViewById(R.id.itemIcon);
        TextView itemTitle = (TextView) convertView.findViewById(R.id.itemTitle);
        if (iconSize > 0) {
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) itemIcon.getLayoutParams();
            layoutParams.width = iconSize;
            layoutParams.height = iconSize;

            layoutParams.leftMargin = DeviceUtils.dpToPx(8);
            layoutParams.rightMargin = DeviceUtils.dpToPx(8);
            itemIcon.setLayoutParams(layoutParams);
        }

        if (!TextUtils.isEmpty(popupMenuIconItem.iconUrl)) {
            if (iconPreRes > 0) {
                itemIcon.setImageResource(iconPreRes);
            } else if(popupMenuIconItem.icon > 0) {
                itemIcon.setImageResource(popupMenuIconItem.icon);
            }
            NightQImageLoader.displayPhotoWithUrl(popupMenuIconItem.iconUrl, itemIcon);
            itemIcon.setVisibility(View.VISIBLE);
        } else if (popupMenuIconItem.icon > 0) {
            itemIcon.setImageResource(popupMenuIconItem.icon);
            itemIcon.setVisibility(View.VISIBLE);
        } else {
            itemIcon.setVisibility(View.GONE);
            if (!TextUtils.isEmpty(popupMenuIconItem.title)) {
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) itemTitle.getLayoutParams();
                layoutParams.width = LinearLayout.LayoutParams.MATCH_PARENT;
                itemTitle.setLayoutParams(layoutParams);
            }
        }

        if (!TextUtils.isEmpty(popupMenuIconItem.title)) {
            itemTitle.setText(popupMenuIconItem.title);
            itemTitle.setVisibility(View.VISIBLE);
        } else {
            itemTitle.setVisibility(View.GONE);
            if (popupMenuIconItem.icon > 0 || !TextUtils.isEmpty(popupMenuIconItem.iconUrl)) {
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) itemIcon.getLayoutParams();
                layoutParams.width = LinearLayout.LayoutParams.MATCH_PARENT;
                itemIcon.setLayoutParams(layoutParams);
            }
        }
        convertView.setId(popupMenuIconItem.id);
        convertView.setOnClickListener(onClickListener);
        if(isScreenBottom) {
            convertView.setBackgroundResource(backBottomItemResId);
        }
        if(popupMenuIconItem.tag != null) {
            convertView.setTag(popupMenuIconItem.tag);
        }
        if (list != null) {
            list.addView(convertView, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        }
    }


    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(//v,
                        v.getId(), v.getTag());
            }
            dismiss();
        }
    };

    @Override
    public void show() {
        super.show();
        if (mView != null) {
            mView.setEnabled(false);
        }
    }

    @Override
    public void dismiss() {
        if (mView != null) {
            mView.setEnabled(true);
        }
        super.dismiss();
    }

    /**
     * Interface definition for a callback to be invoked when an item in this
     * AdapterView has been clicked.
     */
    public interface OnItemClickListener {

        /**
         * Callback method to be invoked when an item in this AdapterView has
         * been clicked.
         * <p>
         * Implementers can call getItemAtPosition(position) if they need
         * to access the data associated with the selected item.
         *
         * @param id The row id of the item that was clicked.
         */
        void onItemClick(//View view,
                         int id,
                         Object tag);
    }
}
