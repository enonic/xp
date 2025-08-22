package com.enonic.xp.descriptor;

import java.util.Objects;

import com.enonic.xp.app.ApplicationKey;

public abstract class Descriptor
{
    private final DescriptorKey key;

    public Descriptor( final DescriptorKey key )
    {
        Objects.requireNonNull( key, "key cannot be null" );
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
