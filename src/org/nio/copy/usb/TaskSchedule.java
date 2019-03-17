package org.nio.copy.usb;

import java.util.concurrent.*;


public class TaskSchedule {

    private static BlockingQueue mWorkQueue = new LinkedBlockingDeque();
    private static ThreadPoolExecutor mThreadPool = null;
    private static int MAX_THREAD_SIZE = 16;
    private static int CORE_THREAD_SIZE = 8;
    static {
        init();
    }

    static void init() {
        CORE_THREAD_SIZE = Runtime.getRuntime().availableProcessors();
        System.out.println("Core Thread Size: " + CORE_THREAD_SIZE);
        //CORE_THREAD_SIZE = 0;
        mThreadPool = new ThreadPoolExecutor(CORE_THREAD_SIZE, MAX_THREAD_SIZE, 20L, TimeUnit.SECONDS, mWorkQueue);
        mThreadPool.allowCoreThreadTimeOut(true);
    }

    public static void execute(final ITask task) {
        mThreadPool.execute(() -> {
            task.execute();
        });
    }

    public static void cancel(ITask task) {
        if (!task.isCancel()) {
            task.cancel();
        }
    }

    public static void cancelAll() {
        mWorkQueue.clear();
    }

    public static void shutdown() {
        mThreadPool.shutdown();
    }
}
