import org.nio.copy.usb.CopyService;
import org.nio.copy.usb.IListener;
import org.nio.copy.usb.ITask;

import java.io.File;

public class Main {
    private static long mStartTime;
    private static long mCompleteTime;
    //public static final String SRC_PATH = "/home/quenlen/Test/src_one";
    //public static final String SRC_PATH = "/home/quenlen/Test/src";
    //public static final String SRC_PATH = "/home/quenlen/Downloads";
    //public static final String SRC_PATH = "/home/quenlen/Android/Sdk";
    public static final String SRC_PATH = "/Users/alex.liu/Downloads/Work/Log/NDMS-1218";
    public static final String DEST_PATH  = "/Users/alex.liu/Downloads/Work/Log/Test";
    public static void main(String[] args) {
        final File src = new File(SRC_PATH);
        final File dest = new File(DEST_PATH);
        getRunnable(src, dest).run();
        System.out.println("Main Thread end!!!");
    }

    public static Runnable getRunnable(final File src, final File dest) {
        return () -> {
            ITask task = CopyService.createTask(src, dest, 0,
                    new IListener() {
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
                        public void onCancel() {
                            System.out.println("onCancel");
                        }

                        @Override
                        public void onProgress(long cur, long total) {
                            System.out.println("onProgress: total = " + total + " \tcur = " + cur);
                        }
                    }
            );

            mStartTime = System.currentTimeMillis();
            System.out.println("Start: get task load");
            long taskLoad = task.getTaskLoad();
            mCompleteTime = System.currentTimeMillis();
            System.out.println("End get task load: Total time: " + (mCompleteTime - mStartTime) + " \tTask Load: " + taskLoad);
            task.execute();

            /*try {
                System.out.println("Wait to cancel!!");
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("Start Cancel");
            task.cancel();
            System.out.println("End cancel!!");*/
        };
    }
}
