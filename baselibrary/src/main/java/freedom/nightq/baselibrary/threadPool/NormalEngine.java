package freedom.nightq.baselibrary.threadPool;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by Nightq on 14-6-5.
 */
public class NormalEngine extends BaseEngineForNightQ {
    private static NormalEngine mNormalEngine;

    public static String nameFront = "normal-pool-";
    public static int priority = Thread.NORM_PRIORITY;
    public static int corePoolSize = Runtime.getRuntime().availableProcessors() * 2;
    public static int maximunPoolSize = (int) (corePoolSize * 1.5);
    public static long keepAliveTime = 10000L;

    public static NormalEngine getInstance () {
        if (mNormalEngine == null) {
            mNormalEngine = new NormalEngine();
        }
        return mNormalEngine;
    }

    private NormalEngine() {
        super(corePoolSize, maximunPoolSize, priority, nameFront, keepAliveTime, TimeUnit.MILLISECONDS);
    }


}
