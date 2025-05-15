package com.enonic.xp.impl.task.script;

import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.task.TaskDescriptor;

public interface NamedTaskFactory
{
    NamedTask create( TaskDescriptor descriptor, PropertyTree data );

}
