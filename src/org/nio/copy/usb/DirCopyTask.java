package org.nio.copy.usb;


import java.io.File;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class DirCopyTask extends AsyncTask implements ParentTask{

    private File mSource;
    private File mTarget;
    private long mTaskLoad = -1;

    public DirCopyTask(ParentTask parent, File source, File dest) {
        super(parent);
        init(source, dest);
    }

    @Override
    public final void onChildTaskUpdateProgress(ITask child, long progress) {
        notifyNewProgress(progress);
    }

    @Override
    public String getName() {
        return mSource.getName();
    }

    private void init(File source, File dest) {
        if (!source.isDirectory()) {
            throw new RuntimeException("Source must is directory!!!");
        }
        mSource = source;
        mTarget = dest;
        File[] children = mSource.listFiles();
        for (File child: children) {
            ITask subTask;
            if (child.isDirectory()) {
                subTask = new DirCopyTask(this, child, new File(mTarget + File.separator + child.getName()));
            } else {
                subTask = new FileCopyTask(this, child, new File(mTarget + File.separator + child.getName()));
            }
            mChildren.add(subTask);
        }
    }

    private List<ITask> mChildren = new CopyOnWriteArrayList<>();


    public final List<ITask> getChildren() {
        return mChildren;
    }

    private static final String TAG = "DirCopyTask";

    @Override
    public final void onChildTaskComplete(ITask child, int flagState) {
        //System.out.println(TAG + ": onChildTaskComplete: " + child.getName() + " TaskLoad: " + child.getTaskLoad() + " state: " + flagState);
        if (flagState == FLAG_STATE_ERROR && EXECUTE_MODE_STRICT == getExecuteMode()) {
            for (ITask task: getChildren()) {
                TaskSchedule.cancel(task);
            }
            setState(FLAG_STATE_ERROR);
            onCompleteExecute(FLAG_STATE_ERROR);
        }
    }

    @Override
    public int getExecuteMode() {
        return EXECUTE_MODE_STRICT;
    }

    @Override
    protected void onCompleteExecute(int state) {
        super.onCompleteExecute(state);
    }

    @Override
    public final synchronized long getTaskLoad() {
        if (mTaskLoad == -1) {
            long taskLoad = 0;
            for (ITask child : getChildren()) {
                taskLoad += child.getTaskLoad();
            }
            mTaskLoad = taskLoad;
        }
        return mTaskLoad;
    }

    @Override
    protected final int onExecute() {
        if (mChildren == null || mChildren.isEmpty()) {
            return FLAG_STATE_COMPLETE;
        } else {
            for (ITask child: mChildren) {
                TaskSchedule.execute(child);
            }
        }
        return FLAG_STATE_EXECUTING;
    }

    @Override
    protected final void onCanceled() {
        super.onCanceled();
        for (ITask task: mChildren) {
            TaskSchedule.cancel(task);
        }
    }
}
