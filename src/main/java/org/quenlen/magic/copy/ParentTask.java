package org.quenlen.magic.copy;

interface ParentTask {

    /**
     * Execute mode: strict mode.
     * if has one child task failure, will cancel all child and stop this task.
     * call {@link Task#onCompleteExecute(int)} with {@link ITask#FLAG_STATE_ERROR}
     */
    int EXECUTE_MODE_STRICT = 0;

    /**
     * Execute mode: non strict mode.
     *
     * If has child task failure, will ignore failure child and continue other child task.
     * until all child task complete and then call {@link Task#onCompleteExecute(int)}
     */
    int EXECUTE_MODE_NON_STRICT = 1;

    String getName();
    /**
     * After a child task complete, call this method to notify parent task to
     * update child state.
     * @param child complete child task.
     * @param flagState complete state. the end state. one of {@link ITask#FLAG_STATE_CANCEL}，
     *                  {@link ITask#FLAG_STATE_ERROR} or {@link ITask#FLAG_STATE_COMPLETE}
     */
    void onChildTaskComplete(Task child, int flagState);

    /**
     * To receive child task execute progress.
     * @param child
     * @param increase
     * @param current
     * @param total
     */
    void onChildTaskUpdateProgress(Task child, long increase, long current, long total);

    /**
     * Get the execute mode.
     * 1. {@link #EXECUTE_MODE_STRICT}, child task can't failure, if find child task failure,
     *      will stop all task exclude has been execute.
     * 2. {@link #EXECUTE_MODE_NON_STRICT}, child task can failure, not confluence other task.
     * @return
     */
    int getExecuteMode();

    TaskSchedule getSchedule();
}
