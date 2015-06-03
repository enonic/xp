package com.enonic.xp.schema;

import org.apache.commons.lang.StringUtils;

import com.google.common.annotations.Beta;
import com.google.common.base.Joiner;

import com.enonic.xp.module.ModuleKey;

@Beta
public abstract class BaseSchemaName
{
    private final static String SEPARATOR = ":";

    private final String refString;

    private final ModuleKey moduleKey;

    private final String localName;

    protected BaseSchemaName( final String name )
    {
        this.moduleKey = ModuleKey.from( StringUtils.substringBefore( name, SEPARATOR ) );
        this.localName = StringUtils.substringAfter( name, SEPARATOR );
        this.refString = Joiner.on( SEPARATOR ).join( this.moduleKey.toString(), this.localName );
    }

    protected BaseSchemaName( final ModuleKey moduleKey, final String localName )
    {
        this.moduleKey = moduleKey;
        this.localName = localName;
        this.refString = Joiner.on( SEPARATOR ).join( this.moduleKey.toString(), this.localName );
    }

    public String getLocalName()
    {
        return localName;
    }

    public ModuleKey getModuleKey()
    {
        return moduleKey;
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

        final BaseSchemaName moduleBasedName = (BaseSchemaName) o;

        return refString.equals( moduleBasedName.refString );
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
