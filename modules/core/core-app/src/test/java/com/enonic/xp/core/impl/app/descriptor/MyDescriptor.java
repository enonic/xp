package com.enonic.xp.core.impl.app.descriptor;

import com.enonic.xp.descriptor.Descriptor;
import com.enonic.xp.descriptor.DescriptorKey;

public final class MyDescriptor
    extends Descriptor
{
    public MyDescriptor( final String key )
    {
        super( DescriptorKey.from( key ) );
    }
}
