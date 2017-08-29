package com.enonic.xp.task;

import com.google.common.annotations.Beta;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.descriptor.Descriptors;

@Beta
public interface TaskDescriptorService
{
    Descriptors<TaskDescriptor> getTasks();

    Descriptors<TaskDescriptor> getTasks( ApplicationKey app );
}
