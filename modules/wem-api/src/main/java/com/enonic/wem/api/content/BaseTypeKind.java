package com.enonic.wem.api.content;

import java.util.Map;

import com.google.common.collect.Maps;

public enum BaseTypeKind
{

    CONTENT_TYPE( "ContentType" ),
    MIXIN( "Mixin" ),
    RELATIONSHIP_TYPE( "RelationshipType" );

    private static final Map<String, BaseTypeKind> lookupTable = Maps.newHashMap();

    static
    {
        for ( BaseTypeKind baseTypeKind : BaseTypeKind.values() )
        {
            lookupTable.put( baseTypeKind.id, baseTypeKind );
        }
    }

    private final String id;

    private BaseTypeKind( final String id )
    {
        this.id = id;
    }

    @Override
    public String toString()
    {
        return this.id;
    }

    public static BaseTypeKind from( final String value )
    {
        if ( value == null )
        {
            return null;
        }
        return lookupTable.get( value );
    }
}
