package com.enonic.xp.schema;

import com.google.common.base.Preconditions;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.app.ApplicationKey;

@PublicApi
public abstract class BaseSchemaName
{
    private final static String SEPARATOR = ":";

    private final String refString;

    private final ApplicationKey applicationKey;

    private final String localName;

    protected BaseSchemaName( final String name )
    {
        Preconditions.checkNotNull( name, "BaseSchemaName can't be null" );
        final int index = name.indexOf( SEPARATOR );
        this.applicationKey = ApplicationKey.from( index == -1 ? name : name.substring( 0, index ) );
        this.localName = index == -1 ? "" : name.substring( index + 1 );
        this.refString = this.applicationKey + SEPARATOR + this.localName;
    }

    protected BaseSchemaName( final ApplicationKey applicationKey, final String localName )
    {
        this.applicationKey = applicationKey;
        this.localName = localName;
        this.refString = this.applicationKey + SEPARATOR + this.localName;
    }

    public String getLocalName()
    {
        return localName;
    }

    public ApplicationKey getApplicationKey()
    {
        return applicationKey;
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

        final BaseSchemaName applicationBasedName = (BaseSchemaName) o;

        return refString.equals( applicationBasedName.refString );
    }

    @Override
    public int hashCode()
    {
        return refString.hashCode();
    }

    @Override
    public String toString()
    {
        return refString;
    }
}
