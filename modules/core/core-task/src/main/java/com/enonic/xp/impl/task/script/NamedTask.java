package com.enonic.xp.impl.task.script;

import com.enonic.xp.task.RunnableTask;
import com.enonic.xp.task.TaskDescriptor;

public interface NamedTask
    extends RunnableTask
{
    TaskDescriptor getTaskDescriptor();
}
