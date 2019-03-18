package org.nio.copy.usb;

import java.util.Map;
import java.util.concurrent.*;


class TaskSchedule {

    private  BlockingQueue mWorkQueue = new LinkedBlockingDeque();
    private  ThreadPoolExecutor mThreadPool;
    private static int MAX_THREAD_SIZE = 16;
    private static int CORE_THREAD_SIZE = 8;
    private  Map<String, Runnable> mExecuteMap = new ConcurrentHashMap<>();

    public TaskSchedule(int desireThreadSize) {
        if (desireThreadSize < 0) {
            throw new IllegalArgumentException("Desire thread size can't less than zero");
        }
        MAX_THREAD_SIZE =  Runtime.getRuntime().availableProcessors();
        CORE_THREAD_SIZE = desireThreadSize > MAX_THREAD_SIZE ? MAX_THREAD_SIZE : desireThreadSize;
        System.out.println("Core Thread Size: " + CORE_THREAD_SIZE);
        //CORE_THREAD_SIZE = 0;
        mThreadPool = new ThreadPoolExecutor(CORE_THREAD_SIZE, MAX_THREAD_SIZE, 20L, TimeUnit.SECONDS, mWorkQueue);
        mThreadPool.allowCoreThreadTimeOut(true);
    }

    public  void execute(final ITask task) {
        Runnable runnable = () -> {
            mExecuteMap.remove(task.getName());
            task.execute();
        };
        mExecuteMap.put(task.getName(), runnable);
        mThreadPool.execute(runnable);
    }


    public void cancel() {
        mWorkQueue.clear();
        mExecuteMap.clear();
    }

    public  void shutdown() {
        cancel();
        mThreadPool.shutdown();
    }
}
