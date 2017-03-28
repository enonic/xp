package com.enonic.xp.descriptor;

import com.google.common.base.Preconditions;

import com.enonic.xp.app.ApplicationKey;

public abstract class Descriptor
{
    private final DescriptorKey key;

    public Descriptor( final DescriptorKey key )
    {
        Preconditions.checkNotNull( key, "key cannot be null" );
        this.key = key;
    }

    public final DescriptorKey getKey()
    {
        return this.key;
    }

    public final String getName()
    {
        return this.key.getName();
    }

    public final ApplicationKey getApplicationKey()
    {
        return this.key.getApplicationKey();
    }
}
