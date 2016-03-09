package freedom.nightq.puzzlepicture.model;

/**
 * Created by H3c on 5/22/15.
 * 传送数据到图片处理的地方
 * @edit nightq
 */
public class ToProcessPicEvent {
    public ProcessComposeModel mData;
    public int position;

    public ToProcessPicEvent(ProcessComposeModel data, int position) {
        this.mData = data;
        this.position = position;
    }

    public void clear() {
        mData = null;
        position = 0;
    }
}
