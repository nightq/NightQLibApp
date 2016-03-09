package freedom.nightq.baselibrary.adapter;

import android.support.v7.widget.RecyclerView;

import java.util.Collections;
import java.util.List;


/**
 * Created by NightQ
 */
public abstract class BaseRecyclerViewAdapter<T, VH extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<VH> {

    public BaseRecyclerViewAdapter() {
    }

    private List<T> items = Collections.EMPTY_LIST;

    public List<T> getItems() {
        return items;
    }

    public void setItems(List<T> items) {
        this.items = items;
    }

    public T getItem(int position) {
        return items.get(position);
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

}
