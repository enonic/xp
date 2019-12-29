package com.enonic.xp.task;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.descriptor.Descriptors;

@PublicApi
public interface TaskDescriptorService
{
    Descriptors<TaskDescriptor> getTasks();

    Descriptors<TaskDescriptor> getTasks( ApplicationKey app );
}
