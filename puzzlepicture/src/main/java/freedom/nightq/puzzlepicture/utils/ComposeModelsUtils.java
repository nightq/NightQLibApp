package freedom.nightq.puzzlepicture.utils;

import android.content.Context;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.List;

import freedom.nightq.baselibrary.utils.IoUtils;
import freedom.nightq.puzzlepicture.enums.ComposeType;
import freedom.nightq.puzzlepicture.model.ComposeModel;


/**
 * Created by Nightq on 15/12/4.
 */
public class ComposeModelsUtils {

    private static HashMap<ComposeType, List<ComposeModel>> composeModelsCache;

    /**
     * 版式 raw 文件名
     */
    public static final String Compose_File_2 = "compose2";
    /**
     * 1张图的版式key
     */
    public static final String Compose_Pic_One = "one_pic";
    /**
     * 两张图的版式key
     */
    public static final String Compose_Pic_Two = "two_pic";
    /**
     * 三张图的版式key
     */
    public static final String Compose_Pic_Three = "three_pic";
    /**
     * 四张图的版式key
     */
    public static final String Compose_Pic_Four = "four_pic";

    /**
     * 初始化
     * @param context
     */
    public static void init(Context context) {
        getComposeModelsFromRaw(context);
    }

    /**
     * 从raw  获取区号列表
     *
     * @return
     */
    private synchronized static HashMap<ComposeType, List<ComposeModel>> getComposeModelsFromRaw(
            Context context) {
        if (composeModelsCache != null) {
            return composeModelsCache;
        }
        HashMap<String, List<ComposeModel>> modelListForStrKey = null;
        try {
            int resId = context.getResources().getIdentifier(
                    Compose_File_2, //Compose_File,
                    "raw",
                    context.getPackageName());
            if (resId <= 0) {
                //NOTHING
            } else {
                Reader reader = new InputStreamReader(context.getResources().openRawResource(resId));
                String json = IoUtils.readAllCharsAndClose(reader);
                modelListForStrKey = new Gson().fromJson(json,
                        new TypeToken<HashMap<String, List<ComposeModel>>>() {
                        }.getType());
            }
        } catch (Throwable t) {
            //NOTHING
        }
        if (modelListForStrKey != null) {
            HashMap<ComposeType, List<ComposeModel>> tmpMap = new HashMap<>();
            for (String key : modelListForStrKey.keySet()) {
                tmpMap.put(
                        getComposeTypeFromStr(key), modelListForStrKey.get(key));
            }
            composeModelsCache = tmpMap;
        }
        return composeModelsCache;
    }


    /**
     * 通过版式图片数量取版式列表
     *
     * @param context
     * @param key
     * @return
     */
    public static List<ComposeModel> getComposeModelsByType(
            Context context, ComposeType key) {
        getComposeModelsFromRaw(context);
        if (composeModelsCache == null) {
            throw new IllegalArgumentException();
        }
        return composeModelsCache.get(key);
    }

    /**
     * 通过版式图片数量取版式列表
     *
     * @param context
     * @return
     */
    public static int getComposeModelsTotal(
            Context context, ComposeType composeType) {
        return getComposeModelsByType(context, composeType).size();
    }

    /**
     * get mode from string type
     * @return
     */
    private static ComposeType getComposeTypeFromStr (String composeTypeStr) {
        if (ComposeModelsUtils.Compose_Pic_Four.equalsIgnoreCase(composeTypeStr)) {
            return ComposeType.ComposeFourPic;
        } else if (ComposeModelsUtils.Compose_Pic_Three.equalsIgnoreCase(composeTypeStr)) {
            return ComposeType.ComposeThreePic;
        } else if (ComposeModelsUtils.Compose_Pic_Two.equalsIgnoreCase(composeTypeStr)) {
            return ComposeType.ComposeTwoPic;
        } else {
            return ComposeType.ComposeOnePic;
        }
    }

    /**
     * get mode from string type
     * @return
     */
    public static ComposeType getComposeTypeFromCount (int picCount) {
        // 判断 几个图片
        switch (picCount) {
            case 2:
                return ComposeType.ComposeTwoPic;
            case 3:
                return ComposeType.ComposeThreePic;
            case 4:
                return ComposeType.ComposeFourPic;
            case 0:
            case 1:
            default:
                return ComposeType.ComposeOnePic;
        }
    }

}
