package freedom.nightq.widgets.tagview;

import android.content.Context;
import android.util.AttributeSet;

/**
 * 每组相同点的tag 的集合
 */
public class TagGroupDefaultView extends TagGroupBaseView<TagDirectionBaseView>  {

    /**
     * 创建
     * @param context
     * @param attrs
     * @return
     */
    public static TagGroupDefaultView createInstance (Context context, AttributeSet attrs) {
        return new TagGroupDefaultView(context, attrs);
    }

    public TagGroupDefaultView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
}
