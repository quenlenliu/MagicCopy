package org.nio.copy.usb;

import java.io.File;

public class UsbCopyTask extends DirCopyTask {

    private ICopyListener mListener;

    public UsbCopyTask(File source, File dest) {
        super(null, source, dest);
    }

    public void setListener(ICopyListener mListener) {
        this.mListener = mListener;
    }

    @Override
    protected void onStartExecute() {
        super.onStartExecute();
        final ICopyListener listener = mListener;
        if (listener != null) {
            listener.onStart();
        }
    }

    @Override
    protected void onUpdateProgress(long cur, long total) {
        super.onUpdateProgress(cur, total);
        final ICopyListener listener = mListener;
        if (listener != null) {
            listener.onProgress(cur, total);
        }
    }

    @Override
    protected void onCompleteExecute(int state) {
        super.onCompleteExecute(state);
        if (state == FLAG_STATE_COMPLETE) {
            final ICopyListener listener = mListener;
            if (listener != null) {
                listener.onComplete();
            }
        } else if (state == FLAG_STATE_ERROR) {
            final ICopyListener listener = mListener;
            if (listener != null) {
                listener.onError();
            }
        }
    }

    public StringBuilder printAllTask() {
        StringBuilder sb = new StringBuilder();
        sb.append("Task: " + getName() + " " + getTaskLoad() + "\n");
        for (ITask task: getChildren()) {
            printSubTask(sb, task, 0);
        }
        return sb;
    }

    public StringBuilder printSubTask(StringBuilder sb, ITask iTask, int size) {
        for (int i = 0; i < size; ++i) {
            sb.append("    ");
        }
        sb.append("->").append("Task: " + iTask.getName() + " " + iTask.getTaskLoad() + "\n");
        /*if (iTask.getChildren() != null) {
            for (ITask task: iTask.getChildren()) {
                printSubTask(sb, task, size++);
            }
        }*/
        return sb;
    }
}
