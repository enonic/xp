package com.enonic.xp.core.content;

import java.util.Objects;

import com.google.common.base.Preconditions;

import com.enonic.xp.core.data.PropertyTree;
import com.enonic.xp.core.schema.mixin.MixinName;

public final class Metadata
{
    private MixinName name;

    private PropertyTree data;

    public Metadata( final MixinName name, final PropertyTree data )
    {
        Preconditions.checkNotNull( name, "name cannot be null" );
        Preconditions.checkNotNull( data, "data cannot be null" );
        this.name = name;
        this.data = data;
    }

    public PropertyTree getData()
    {
        return data;
    }

    public void setData( final PropertyTree data )
    {
        this.data = data;
    }

    public MixinName getName()
    {
        return name;
    }

    public void setName( final MixinName name )
    {
        this.name = name;
    }

    public Metadata copy()
    {
        return new Metadata( name, data.copy() );
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

        final Metadata metadata = (Metadata) o;

        if ( !name.equals( metadata.name ) )
        {
            return false;
        }

        if ( !data.equals( metadata.data ) )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( name, data );
    }
}
