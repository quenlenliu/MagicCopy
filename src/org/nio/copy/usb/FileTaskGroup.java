package org.nio.copy.usb;


import java.io.File;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

class FileTaskGroup extends Task implements ParentTask{

    private File mSource;
    private File mTarget;
    private long mTaskLoad = -1;
    private TaskSchedule mSchedule;

    public FileTaskGroup(ParentTask parent, File source, File dest) {
        super(parent);
        mSchedule = parent.getSchedule();
        init(source, dest);
    }

    public FileTaskGroup(File source, File dest, TaskSchedule schedule) {
        super(null);
        mSchedule = schedule;
        init(source, dest);
    }

    @Override
    public final void onChildTaskUpdateProgress(Task child, long progress) {
        notifyNewProgress(progress);
    }

    @Override
    public String toString() {
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
                subTask = new FileTaskGroup(this, child, new File(mTarget + File.separator + child.getName()));
            } else {
                subTask = new FileTask(this, child, new File(mTarget + File.separator + child.getName()));
            }
            mChildren.add(subTask);
        }
    }

    private List<ITask> mChildren = new CopyOnWriteArrayList<>();


    public final List<ITask> getChildren() {
        return mChildren;
    }

    @Override
    public final void onChildTaskComplete(Task child, int flagState) {
        /*
         * check the execute mode is strict mode and has a children task not execute success.
         * Cancel all  task.
         */
        if (flagState == FLAG_STATE_ERROR) {
            if (EXECUTE_MODE_STRICT == getExecuteMode()
                && FLAG_STATE_EXECUTING == getState()) {
                setState(flagState);
                cancelChildren();
                getSchedule().shutdown();
            } else {
                child.ignoreRemainingTask(); // ignore remaining task.
                System.out.println("Task(" + child.getName()+") execute occur error, but execute mode is non strict, ignore it ");
            }
        }
    }


    @Override
    public int getExecuteMode() {
        return EXECUTE_MODE_NON_STRICT;
    }

    public void cancel() {
        super.cancel();
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
                getSchedule().execute(child);
            }
        }
        return FLAG_STATE_EXECUTING;
    }

    @Override
    public TaskSchedule getSchedule() {
        return mSchedule;
    }
}
