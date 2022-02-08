package com.enonic.xp.impl.task.script;

import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.task.TaskDescriptor;

public interface NamedTaskFactory
{
    NamedTask create( TaskDescriptor descriptor, PropertyTree data );

    NamedTask createLegacy( DescriptorKey key, PropertyTree data );
}
