package com.enonic.xp.descriptor;

import com.enonic.xp.app.ApplicationKey;

import static java.util.Objects.requireNonNull;

public abstract class Descriptor
{
    private final DescriptorKey key;

    public Descriptor( final DescriptorKey key )
    {
        requireNonNull( key, "key cannot be null" );
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
