package freedom.nightq.puzzlepicture.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

import freedom.nightq.baselibrary.NightQAppLib;
import freedom.nightq.puzzlepicture.utils.ComposeModelsUtils;
import freedom.nightq.puzzlepicture.utils.DataUtil;

/**
 * Created by Nightq on 15/12/17.
 * 图片处理的model
 */
public class ProcessComposeModel {

    // 版式位置
    public int mComposeModelPos;
    // 版式 model
    public ComposeModel mComposeModel;
    // 图片
    public List<ProcessPicModel> mPicList;

    public ProcessComposeModel() {
    }

    /**
     * 根据本地选图创建model,并初始化
     */
    public ProcessComposeModel(
            LinkedHashMap<String, String> selectedBeans,
            boolean needInit) {
        mPicList = new ArrayList<>(getModelsFromMap(selectedBeans));
        if (needInit) {
            initDefaultCompose();
        }
    }

    /**
     * 加图
     * @param selectedBeans
     */
    public void addPics (LinkedHashMap<String, String> selectedBeans) {
        mPicList.addAll(getModelsFromMap(selectedBeans));
    }

    /**
     * 初始化生成并设置合适的版式
     */
    public void initDefaultCompose () {
        clearCompose();
        mComposeModel = getOptComposeModel();
    }

    /**
     * 获取当前较合适的版式
     * @return
     */
    private ComposeModel getOptComposeModel () {
        // 初始化版式 如果没有版式，就默认推荐一个
        List<ComposeModel> list = ComposeModelsUtils
                .getComposeModelsByType(
                        NightQAppLib.getAppContext(),
                        ComposeModelsUtils.getComposeTypeFromCount(
                                mPicList.size()));
        // 搞半天还是默认0 
//        int targetPosition = 0;
//        switch (mPicList.size()) {
//            case 3:
//                targetPosition = 0;
//                break;
//            case 4:
//                targetPosition = 0;
//                break;
//            case 2:
//                targetPosition = 0;//(new Random().nextInt(list.size()));
//                break;
//        }
//        if (targetPosition >=0
//                && list.size() > targetPosition) {
//            return list.get(targetPosition);
//        }
        return list.get(0);
    }

    /**
     * 从 选图取 list
     * @param selectedBeans
     * @return
     */
    public static List<ProcessPicModel> getModelsFromMap (LinkedHashMap<String, String> selectedBeans) {
        List<String> list = new ArrayList<>();
        if (selectedBeans != null
                && selectedBeans.size() > 0) {
            list.addAll(selectedBeans.values());
        }
        return (DataUtil.generateProcessModelsFromImageBean(list));
    }

    /**
     * 清空版式
     */
    public void setComposeModel (int position, ComposeModel composeModel) {
        mComposeModelPos = position;
        mComposeModel = composeModel;
        if (mPicList != null) {
            for (ProcessPicModel model : mPicList) {
                model.mPosScaModel.clearTransAndScale();
            }
        }
    }

    /**
     * 交换顺序
     */
    public void swapItem (int from, int to) {
        Collections.swap(mPicList, from, to);
    }

    /**ø
     * 清空版式
     */
    public void clearCompose () {
        mComposeModelPos = -1;
        mComposeModel = null;
        if (mPicList != null) {
            for (ProcessPicModel model : mPicList) {
                model.mPosScaModel.clearTransAndScale();
            }
        }
    }

}
