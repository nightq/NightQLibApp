package freedom.nightq.puzzlepicture.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import freedom.nightq.baselibrary.adapter.BaseRecyclerViewAdapter;
import freedom.nightq.baselibrary.utils.imageLoader.NightQImageLoader;
import freedom.nightq.puzzlepicture.R;
import freedom.nightq.puzzlepicture.model.ComposeModel;

/**
 * 滤镜的 adapter
 */
public class ComposeAdapter
        extends BaseRecyclerViewAdapter<ComposeModel, ComposeAdapter.ViewHolder>
        implements View.OnClickListener {

    public OnComposeChangelistener mOnComposeChangelistener;

    private int selectedId = -1;

    public ComposeAdapter() {
        super();
//        currentAnimType = ANIM_SWING_IN_RIGHT;
    }

    /**
     * 这个要记住reset ，不然会不准的
     * @param id
     */
    public void reset(int id) {
        selectedId = id;
    }


    /**
     * getPositionBy Id
     * @param id 找不到返回0
     */
    public int getPositionByComposeId(int id) {
        int result = 0;
        if (id < 0) {
            // normal
            result = 0;
        } else {
            for (int i=0; getItems() != null && i< getItems().size(); i++) {
                if (getItems().get(i).getDrawableId() == id) {
                    result = i;
                    break;
                }
            }
        }
        return result;
    }

    /**
     * 设置监听
     * @param mOnComposeChangelistener
     */
    public void setOnComposeChangelistener(OnComposeChangelistener mOnComposeChangelistener) {
        this.mOnComposeChangelistener = mOnComposeChangelistener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.process_pic_compose_adapter_item, null)
        );
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ComposeModel model = getItem(position);

        NightQImageLoader.displayResourceImage(model.getDrawableId(), holder.mItemIV);
        if(model.getDrawableId() == selectedId) {
            holder.mItemIV.setAlpha(1f);
        } else {
            holder.mItemIV.setAlpha(0.3f);
        }
        holder.mItem.setTag(position);
        holder.mItem.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        int position = (int) view.getTag();
        ComposeModel model = getItem(position);
        if(model.getDrawableId() == selectedId) {
            return;
        }
        if (mOnComposeChangelistener == null
                || mOnComposeChangelistener.onComposeChange(position, model)) {
            // 没有listener 或者成功 就切换
            selectedId = model.getDrawableId();
            notifyDataSetChanged();
        }
    }

    public interface OnComposeChangelistener {
        /**
         * @return boolean success?
         */
        boolean onComposeChange(int position, ComposeModel model);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public RelativeLayout mItem;
        public ImageView mItemIV;

        public ViewHolder(View itemView) {
            super(itemView);
            mItem = (RelativeLayout) itemView.findViewById(R.id.layoutContent);
            mItemIV = (ImageView) itemView.findViewById(R.id.imageView);
        }
    }

}
