package freedom.nightq.libraryapp.tagSample.widgets;

import java.io.Serializable;
import java.util.List;

import freedom.nightq.widgets.tagview.bean.TagInfoBean;

/**
 * Created by Nightq on 16/3/3.
 */
public class TagsModel implements Serializable{
    public List<TagInfoBean> list;

    public TagsModel(List<TagInfoBean> list) {
        this.list = list;
    }
}
