package org.nio.copy.usb;

import java.util.List;

/**
 * Define a base task.
 */
abstract class Task implements ITask{

    private int mState = FLAG_STATE_IDLE;

    protected ParentTask mParent;

    public Task(ParentTask parent) {
        mParent = parent;
    }

    protected int getState() {
        return mState;
    }

    @Override
    public synchronized void setState(int state) {
        if (mState == state) {
            return;
        }
        mState = state;
        switch (state) {
            case FLAG_STATE_EXECUTING:
                onStartExecute();
                break;
            case FLAG_STATE_CANCEL:
            case FLAG_STATE_ERROR:
            case FLAG_STATE_COMPLETE:
                onCompleteExecute(state);
                break;
        }
    }

    @Override
    public final void run() {
        execute();
    }

    public void execute() {
        if (!isCancel()) {
            setState(FLAG_STATE_EXECUTING);
            setState(onExecute());
        }
    }

    public String getName() {
        return mParent == null ? toString() : mParent.getName() + " -> " + toString();
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
        final ParentTask parentTask = getParent();
        if (parentTask != null) {
            parentTask.onChildTaskComplete(this, state);
        }
    }

    @Override
    public List<ITask> getChildren() {
        return null;
    }

    private long mCurrentProgress;
    private Object mLock = new Object();

    void ignoreRemainingTask() {
        notifyNewProgress(getTaskLoad() - mCurrentProgress);
    }

    protected void notifyNewProgress(long progress) {
        synchronized (mLock) {
            if (progress != 0) { // First time and progress changed, send notify.
                final ParentTask parentTask = getParent();
                if (parentTask != null) {
                    parentTask.onChildTaskUpdateProgress(this, progress);
                }
                mCurrentProgress += progress;
                updateProgress(mCurrentProgress, getTaskLoad());
            }
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



    protected final void cancelChildren() {
        if (FLAG_STATE_CANCEL == getState()) {
            // if this task is cancel. don't cancel child again. because child must be canceled;
            return;
        }
        final List<ITask> children = getChildren();
        if (children != null) {
            for (ITask task: children) {
                task.cancel();
            }
        }
    }

    public void cancel() {
        if (mState == FLAG_STATE_CANCEL) {
            throw new RuntimeException("Task(" + getName() + ") has  been canceled, can't cancel again!!!");
        }
        cancelChildren();
        setState(FLAG_STATE_CANCEL);
        onCanceled();
    }
}
