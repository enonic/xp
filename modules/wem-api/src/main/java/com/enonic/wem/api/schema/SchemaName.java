package com.enonic.wem.api.schema;


import com.google.common.base.Joiner;

import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.resource.ResourceKey;

public abstract class SchemaName
{
    protected final static String SEPARATOR = ":";

    private final String refString;

    private final ModuleKey moduleKey;

    private final String localName;

    protected SchemaName( final ModuleKey moduleKey, final String localName )
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

        final SchemaName schemaName = (SchemaName) o;

        return refString.equals( schemaName.refString );
    }

    @Override
    public int hashCode()
    {
        return refString.hashCode();
    }

    public String toString()
    {
        return refString;
    }

    public abstract SchemaKey toSchemaKey();

    public ResourceKey toResourceKey()
    {
        return ResourceKey.from( getModuleKey(), "schema/" + getLocalName() + "/schema.xml" );
    }

}
