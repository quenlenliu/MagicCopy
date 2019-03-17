package org.nio.copy.usb;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;

public interface ITask {
    int FLAG_STATE_ERROR = -1;
    int FLAG_STATE_COMPLETE = 3;
    int FLAG_STATE_CANCEL = 1;
    int FLAG_STATE_EXECUTING = 2;
    int FLAG_STATE_IDLE = 0;

    @Target({ElementType.PARAMETER, ElementType.METHOD})
    @Retention(RetentionPolicy.SOURCE)
    @interface FlagState {

    }

    void execute();
    String getName();
    boolean isCancel();
    void cancel();

    /**
     * Get the task load. include the children task.
     * @return
     */
    long getTaskLoad();
    ParentTask getParent();
    List<ITask> getChildren();
    void setState(@FlagState int state);
}
