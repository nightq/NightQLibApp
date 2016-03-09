package freedom.nightq.baselibrary.threadPool;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by Nightq on 14-6-5.
 */
public class BaseEngineForNightQ {
    private ThreadPoolExecutor mExecutor;
    private LinkedBlockingQueue queue;

    public BaseEngineForNightQ(
            int corePoolSize,
            int maximunPoolSize,
            int priority, String nameFront,
            long keepAliveTime, TimeUnit timeUnit) {
        queue = new LinkedBlockingQueue<Runnable>();
        mExecutor = new ThreadPoolExecutor(
                corePoolSize,
                maximunPoolSize,
                keepAliveTime, timeUnit, queue,
                ThreadFactoryHelper.createThreadFactory(priority, nameFront));
    }

    /** Submits task to execution pool */
    public void submit(final Runnable task) {
//        LogUtils.e("nightq", "queue.size() = " + queue.size());
        mExecutor.execute(task);
    }

    public void removeTask (final Runnable task) {
        if (task != null) {
            mExecutor.remove(task);
        }
    }

}
