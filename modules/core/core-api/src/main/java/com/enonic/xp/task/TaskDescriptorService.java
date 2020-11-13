package com.enonic.xp.task;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.descriptor.Descriptors;
import com.enonic.xp.page.DescriptorKey;

@PublicApi
public interface TaskDescriptorService
{
    Descriptors<TaskDescriptor> getTasks();

    Descriptors<TaskDescriptor> getTasks( ApplicationKey app );

    TaskDescriptor getTask( DescriptorKey key );
}
