package org.nio.copy.usb;

import java.util.List;

/**
 * Define a base task.
 */
public abstract class Task implements ITask{

    private int mState = FLAG_STATE_IDLE;

    protected ParentTask mParent;

    public Task(ParentTask parent) {
        mParent = parent;
    }

    @Override
    public void setState(int state) {
        mState = state;
    }

    public void execute() {
        setState(FLAG_STATE_EXECUTING);
        onStartExecute();
        if (!isCancel()) {
            int flag = onExecute();
            onCompleteExecute(flag);
        } else {
            onCompleteExecute(FLAG_STATE_CANCEL);
        }
    }

    public String getName() {
        return toString();
    }

    /**
     * Get the parent task.
     * @return may be null.
     */
    public ParentTask getParent() {
        return mParent;
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
        setState(state);
        if (mParent != null) {
            mParent.onChildTaskComplete(this, state);
        }
    }

    @Override
    public List<ITask> getChildren() {
        return null;
    }

    private long mCurrentProgress;
    private Object mLock = new Object();
    protected void notifyNewProgress(long progress) {
        synchronized (mLock) {
            final ParentTask parentTask = mParent;
            if (parentTask != null && getChildren() == null) {
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