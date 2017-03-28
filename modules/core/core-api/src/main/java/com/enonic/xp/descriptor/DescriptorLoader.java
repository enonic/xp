package com.enonic.xp.descriptor;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;

public interface DescriptorLoader<T>
{
    Class<T> getType();

    DescriptorKeys find( ApplicationKey key );

    ResourceKey toResource( DescriptorKey key );

    T load( DescriptorKey key, Resource resource )
        throws Exception;

    T createDefault( DescriptorKey key );

    T postProcess( T descriptor );
}
