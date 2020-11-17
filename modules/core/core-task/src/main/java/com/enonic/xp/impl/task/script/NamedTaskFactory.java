package com.enonic.xp.impl.task.script;

import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.page.DescriptorKey;

public interface NamedTaskFactory
{
    NamedTask create( final DescriptorKey key, final PropertyTree data );

    NamedTask createLegacy( final DescriptorKey key, final PropertyTree data );
}
