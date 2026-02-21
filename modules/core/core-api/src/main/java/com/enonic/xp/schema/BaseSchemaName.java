package com.enonic.xp.schema;

import java.util.Objects;

import org.jspecify.annotations.NullMarked;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.descriptor.DescriptorKey;

@PublicApi
@NullMarked
public abstract class BaseSchemaName
{
    private final DescriptorKey descriptorKey;

    protected BaseSchemaName( final String name )
    {
        Objects.requireNonNull( name, "BaseSchemaName cannot be null" );
        this.descriptorKey = DescriptorKey.from( name );
    }

    protected BaseSchemaName( final ApplicationKey applicationKey, final String localName )
    {
        this.descriptorKey = DescriptorKey.from( applicationKey, localName );
    }

    public String getLocalName()
    {
        return this.descriptorKey.getName();
    }

    public ApplicationKey getApplicationKey()
    {
        return this.descriptorKey.getApplicationKey();
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
        final BaseSchemaName that = (BaseSchemaName) o;
        return Objects.equals( descriptorKey, that.descriptorKey );
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode( descriptorKey );
    }

    @Override
    public String toString()
    {
        return descriptorKey.toString();
    }
}
