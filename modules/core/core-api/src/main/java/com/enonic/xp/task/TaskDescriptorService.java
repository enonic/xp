package com.enonic.xp.task;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.descriptor.Descriptors;

@PublicApi
@NullMarked
public interface TaskDescriptorService
{
    Descriptors<TaskDescriptor> getTasks();

    Descriptors<TaskDescriptor> getTasks( ApplicationKey app );

    @Nullable TaskDescriptor getTask( DescriptorKey key );
}
