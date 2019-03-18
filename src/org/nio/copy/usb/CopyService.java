package org.nio.copy.usb;


import java.io.File;

/**
 * The copy service
 */
public class CopyService {


    public static ITask createTask(File src, File dest, int desireThread, IListener listener) {
        UsbCopyTask task =  new UsbCopyTask(src, dest, new TaskSchedule(desireThread));
        task.setListener(listener);
        return task;
    }
}
