package com.enonic.xp.descriptor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

import com.google.common.base.Preconditions;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.core.internal.CharacterChecker;

@PublicApi
public final class DescriptorKey
    implements Serializable
{
    @Serial
    private static final long serialVersionUID = 0;

    private static final String SEPARATOR = ":";

    private final ApplicationKey applicationKey;

    private final String name;

    private DescriptorKey( final ApplicationKey applicationKey, final String name )
    {
        this.applicationKey = Objects.requireNonNull( applicationKey );
        Preconditions.checkArgument( !name.isBlank(), "Descriptor name cannot be blank" );
        this.name = CharacterChecker.check( name, "Invalid name for DescriptorKey [%s]" );
    }

    public ApplicationKey getApplicationKey()
    {
        return applicationKey;
    }

    public String getName()
    {
        return name;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }
        final DescriptorKey that = (DescriptorKey) o;
        return applicationKey.equals( that.applicationKey ) && name.equals( that.name );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( applicationKey, name );
    }

    @Override
    public String toString()
    {
        return applicationKey.toString() + SEPARATOR + name;
    }

    public static DescriptorKey from( final String key )
    {
        Objects.requireNonNull( key, "DescriptorKey cannot be null" );
        final int index = key.indexOf( SEPARATOR );
        if ( index == -1 )
        {
            throw new IllegalArgumentException( "DescriptorKey must contain application key and descriptor name" );
        }
        final String applicationKey = key.substring( 0, index );
        final String descriptorName = key.substring( index + 1 );
        return new DescriptorKey( ApplicationKey.from( applicationKey ), descriptorName );
    }

    public static DescriptorKey from( final ApplicationKey applicationKey, final String descriptorName )
    {
        return new DescriptorKey( applicationKey, descriptorName );
    }
}
