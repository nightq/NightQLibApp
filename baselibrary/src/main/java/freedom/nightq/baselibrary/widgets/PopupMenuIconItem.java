package freedom.nightq.baselibrary.widgets;

import freedom.nightq.baselibrary.utils.ResourceUtils;

/**
 * 存储每一个菜单的数据的。
 */
public class PopupMenuIconItem {
    public int icon;
    public String iconUrl;
    public String title;
    public int titleId;
    public Object tag;
    public int id;
//        boolean visible;

    public PopupMenuIconItem(int icon, String title, int id) {
        this.icon = icon;
        this.title = title;
        this.id = id;
    }

    public PopupMenuIconItem(int icon, int titleId, int id) {
        this.icon = icon;
        this.titleId = titleId;
        this.title = ResourceUtils.getResource().getString(titleId);
        this.id = id;
    }

    /**
     * @param icon 当Url不存在的时候默认显示本地Resource的id
     * @param iconUrl 图片网址
     * @param title
     * @param id
     * @param tag
     */
    public PopupMenuIconItem(int icon, String iconUrl, String title, int id, Object tag) {
        this.icon = icon;
        this.iconUrl = iconUrl;
        this.title = title;
        this.id = id;
        this.tag = tag;
    }
}
