import org.nio.copy.usb.ICopyListener;
import org.nio.copy.usb.UsbCopyTask;

import java.io.File;

public class Main {
    private static long mStartTime;
    private static long mCompleteTime;
    //public static final String SRC_PATH = "/home/quenlen/Test/src_one";
    //public static final String SRC_PATH = "/home/quenlen/Test/src";
    //public static final String SRC_PATH = "/home/quenlen/Downloads";
    public static final String SRC_PATH = "/home/quenlen/Android/Sdk";
    public static final String DEST_PATH  = "/home/quenlen/Test/dest";
    public static void main(String[] args) {
        final File src = new File(SRC_PATH);
        final File dest = new File(DEST_PATH);
        getRunnable(src, dest).run();
        System.out.println("Main Thread end!!!");

    }

    public static Runnable getRunnable(final File src, final File dest) {
        return () -> {
            UsbCopyTask task = new UsbCopyTask(src, dest);
            task.setListener(new ICopyListener() {
                @Override
                public void onStart() {
                    mStartTime = System.currentTimeMillis();
                    System.out.println("onStart");
                }

                @Override
                public void onComplete() {
                    System.out.println("onComplete");
                    mCompleteTime = System.currentTimeMillis();
                    System.out.println("Cost time: " + (mCompleteTime - mStartTime));
                }

                @Override
                public void onError() {
                    System.out.println("onError");
                }

                @Override
                public void onProgress(long cur, long total) {
                    System.out.println("onProgress: total = " + total + " \tcur = " + cur);
                }

            });

            mStartTime = System.currentTimeMillis();
            System.out.println("Start: ");
            long taskLoad = task.getTaskLoad();
            System.out.println(task.printAllTask());
            mCompleteTime = System.currentTimeMillis();
            System.out.println("Total time: " + (mCompleteTime - mStartTime) + " \tTask Load: " + taskLoad);
            task.execute();
        };
    }
}
