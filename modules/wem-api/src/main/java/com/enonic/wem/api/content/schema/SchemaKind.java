package com.enonic.wem.api.content.schema;

import java.util.Map;

import com.google.common.collect.Maps;

public enum SchemaKind
{

    CONTENT_TYPE( "ContentType" ),
    MIXIN( "Mixin" ),
    RELATIONSHIP_TYPE( "RelationshipType" );

    private static final Map<String, SchemaKind> lookupTable = Maps.newHashMap();

    static
    {
        for ( SchemaKind schemaKind : SchemaKind.values() )
        {
            lookupTable.put( schemaKind.id, schemaKind );
        }
    }

    private final String id;

    private SchemaKind( final String id )
    {
        this.id = id;
    }

    @Override
    public String toString()
    {
        return this.id;
    }

    public static SchemaKind from( final String value )
    {
        if ( value == null )
        {
            return null;
        }
        return lookupTable.get( value );
    }
}
