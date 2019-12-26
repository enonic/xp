package com.enonic.xp.content;

import java.util.Objects;

import com.google.common.base.Preconditions;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.schema.xdata.XDataName;

@PublicApi
public final class ExtraData
{
    private XDataName name;

    private PropertyTree data;

    public ExtraData( final XDataName name, final PropertyTree data )
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

    public XDataName getName()
    {
        return name;
    }

    public String getApplicationPrefix()
    {
        return name.getApplicationPrefix();
    }

    public static ApplicationKey fromApplicationPrefix( final String applicationPrefixName )
    {
        return ApplicationKey.from( applicationPrefixName, "-" );
    }

    public void setName( final XDataName name )
    {
        this.name = name;
    }

    public ExtraData copy()
    {
        return new ExtraData( name, data.copy() );
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

        final ExtraData extraData = (ExtraData) o;

        if ( !name.equals( extraData.name ) )
        {
            return false;
        }

        return data.equals( extraData.data );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( name, data );
    }
}
