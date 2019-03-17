package org.nio.copy.usb;

public interface ParentTask {

    int EXECUTE_MODE_STRICT = 0;
    int EXECUTE_MODE_NON_STRICT = 1;
    /**
     * After a child task complete, call this method to notify parent task to
     * update child state.
     * @param child complete child task.
     * @param flagState complete state.
     */
    void onChildTaskComplete(ITask child, int flagState);

    void onChildTaskUpdateProgress(ITask child, long progress);
    /**
     * Get the execute mode.
     * 1. {@link #EXECUTE_MODE_STRICT}, child task can't failure, if find child task failure,
     *      will stop all task exclude has been execute.
     * 2. {@link #EXECUTE_MODE_NON_STRICT}, child task can failure, not confluence other task.
     * @return
     */
    int getExecuteMode();
}
