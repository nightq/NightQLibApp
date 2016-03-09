package freedom.nightq.baselibrary.utils;

import freedom.nightq.baselibrary.NightQAppLib;

/**
 * 语言和区域的
 * Created by Nightq on 15/4/27.
 */
public class LangRegionUtil {

    public static boolean isEnglish() {
        return NightQAppLib.getAppContext().getResources().getConfiguration().locale.getLanguage()
                .equalsIgnoreCase("en");
    }

    public static boolean isMainland() {
        return NightQAppLib.getAppContext().getResources().getConfiguration().locale.getLanguage()
                .equalsIgnoreCase("zh")
                && NightQAppLib.getAppContext().getResources().getConfiguration().locale.getCountry()
                .equalsIgnoreCase("CN");
    }

    public static boolean isTaiwan() {
        return NightQAppLib.getAppContext().getResources().getConfiguration().locale.getLanguage()
                .equalsIgnoreCase("zh")
                && NightQAppLib.getAppContext().getResources().getConfiguration().locale.getCountry()
                .equalsIgnoreCase("TW");
    }

}
