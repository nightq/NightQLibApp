package freedom.nightq.puzzlepicture.utils;


import java.util.ArrayList;
import java.util.List;

import freedom.nightq.puzzlepicture.model.ProcessPicModel;

/**
 * Created by Nightq on 15/12/10.
 */
public class DataUtil {

    /**
     * ProcessPicModel 和 SimpleLocalImageBean 的转换
     * @param dataList
     * @return
     */
    public static List<ProcessPicModel> generateProcessModelsFromImageBean (
            List<String> dataList
    ) {
        List<ProcessPicModel> resultList = new ArrayList<>();
        ProcessPicModel model;
        for(int i=0; dataList != null && i<dataList.size(); i++) {
            model = new ProcessPicModel();
            model.setImagePath(dataList.get(i));
            model.position = i;
            resultList.add(model);
        }
        return resultList;
    }

}
