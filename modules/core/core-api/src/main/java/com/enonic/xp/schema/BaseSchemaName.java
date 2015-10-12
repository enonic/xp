package com.enonic.xp.schema;

import org.apache.commons.lang3.StringUtils;

import com.google.common.annotations.Beta;
import com.google.common.base.Joiner;

import com.enonic.xp.app.ApplicationKey;

@Beta
public abstract class BaseSchemaName
{
    private final static String SEPARATOR = ":";

    private final String refString;

    private final ApplicationKey applicationKey;

    private final String localName;

    protected BaseSchemaName( final String name )
    {
        this.applicationKey = ApplicationKey.from( StringUtils.substringBefore( name, SEPARATOR ) );
        this.localName = StringUtils.substringAfter( name, SEPARATOR );
        this.refString = Joiner.on( SEPARATOR ).join( this.applicationKey.toString(), this.localName );
    }

    protected BaseSchemaName( final ApplicationKey applicationKey, final String localName )
    {
        this.applicationKey = applicationKey;
        this.localName = localName;
        this.refString = Joiner.on( SEPARATOR ).join( this.applicationKey.toString(), this.localName );
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
