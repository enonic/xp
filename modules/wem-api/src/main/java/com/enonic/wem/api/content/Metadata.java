package com.enonic.wem.api.content;

import java.util.Objects;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.data.PropertyTree;
import com.enonic.wem.api.schema.metadata.MetadataSchemaName;

public final class Metadata
{
    private MetadataSchemaName name;

    private PropertyTree data;

    public Metadata( final MetadataSchemaName name, final PropertyTree data )
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

    public MetadataSchemaName getName()
    {
        return name;
    }

    public void setName( final MetadataSchemaName name )
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
