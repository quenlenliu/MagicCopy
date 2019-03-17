package org.nio.copy.usb;

import java.util.List;

public abstract class AsyncTask implements ITask {

    private ParentTask mParent;

    private int mState = FLAG_STATE_IDLE;


    public AsyncTask(ParentTask parent) {
        mParent = parent;
    }

    @Override
    public void setState(int state) {

    }

    @Override
    public String getName() {
        return toString();
    }

    @Override
    public List<ITask> getChildren() {
        return null;
    }

    @Override
    public ParentTask getParent() {
        return mParent;
    }

    @Override
    public void execute() {
        setState(FLAG_STATE_EXECUTING);
        onStartExecute();
        int flag = onExecute();
        if (flag == FLAG_STATE_COMPLETE) {
            setState(FLAG_STATE_COMPLETE);
            onCompleteExecute(FLAG_STATE_COMPLETE);
        } else if (flag == FLAG_STATE_ERROR) {
            setState(FLAG_STATE_ERROR);
            onCompleteExecute(FLAG_STATE_ERROR);
        }
    }


    /**
     * This method must impl by your self for do real task.
     *
     * You can call {@link #updateProgress(long, long)} to notify you progress.
     *
     * You should check the {@link #isCancel()} to stop your task by yourself.
     *
     * You can not start a new thread to do your task.
     *
     *
     * @return a flag state value, to indicate the task's state when finished.
     */
    protected abstract @FlagState int onExecute();

    /**
     * Execute before {@link #onExecute()}
     */
    protected void onStartExecute() {

    }


    protected void onCompleteExecute(@FlagState int state) {
        if (mParent != null) {
            mParent.onChildTaskComplete(this, state);
        }
    }

    private Object mLock = new Object();
    private long mCurrentProgress = 0;

    protected void notifyNewProgress(long progress) {
        synchronized (mLock) {
            final ParentTask parentTask = mParent;
            if (parentTask != null) {
                parentTask.onChildTaskUpdateProgress(this, progress);
            }
            mCurrentProgress += progress;
            updateProgress(mCurrentProgress, getTaskLoad());
        }
    }

    /**
     * Should be called in the method {@link #onExecute()} by the impl class.
     * @param cur
     * @param total
     */
    private void updateProgress(long cur, long total) {
        onUpdateProgress(cur, total);
        if (cur >= total && mState != FLAG_STATE_COMPLETE) {
            onCompleteExecute(FLAG_STATE_COMPLETE);
        }
    }

    /**
     * Receive the task running progress.
     * @param cur
     * @param total
     */
    protected void onUpdateProgress(long cur, long total) {

    }

    /**
     * Called when user cancel this task.
     */
    protected void onCanceled() {

    }

    public final boolean isCancel() {
        return mState == FLAG_STATE_CANCEL;
    }

    public final void cancel() {
        if (mState == FLAG_STATE_CANCEL) {
            throw new RuntimeException("Task has been canceled, can cancel again!!!");
        }
        mState = FLAG_STATE_CANCEL;
        onCanceled();
    }
}
