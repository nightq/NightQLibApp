package freedom.nightq.baselibrary.threadPool;

import java.util.concurrent.TimeUnit;

/**
 * 低优先级的人去队列
 * Created by Nightq on 14-6-5.
 */
public class BackTaskEngine extends BaseEngineForNightQ {
    private static BackTaskEngine mBackTaskEngine;

    public static BackTaskEngine getInstance () {
        if (mBackTaskEngine == null) {
            mBackTaskEngine = new BackTaskEngine();
        }
        return mBackTaskEngine;
    }

    public static String nameFront = "back-pool-";
    public static int priority = Thread.NORM_PRIORITY - 1;
    public static int corePoolSize = 3;
    public static int maximunPoolSize = 3;
    public static long keepAliveTime = 1000L;

    private BackTaskEngine() {
        super(corePoolSize, maximunPoolSize, priority, nameFront, keepAliveTime, TimeUnit.MILLISECONDS);
    }


}
