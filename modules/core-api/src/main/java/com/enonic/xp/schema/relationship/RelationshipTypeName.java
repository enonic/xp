package com.enonic.xp.schema.relationship;

import com.google.common.annotations.Beta;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.schema.BaseSchemaName;

@Beta
public final class RelationshipTypeName
    extends BaseSchemaName
{
    public static final RelationshipTypeName REFERENCE = new RelationshipTypeName( ApplicationKey.SYSTEM, "reference" );

    public static final RelationshipTypeName PARENT = new RelationshipTypeName( ApplicationKey.SYSTEM, "parent" );

    private RelationshipTypeName( final String name )
    {
        super( name );
    }

    private RelationshipTypeName( final ApplicationKey applicationKey, final String localName )
    {
        super( applicationKey, localName );
    }

    public static RelationshipTypeName from( final ApplicationKey applicationKey, final String localName )
    {
        return new RelationshipTypeName( applicationKey, localName );
    }

    public static RelationshipTypeName from( final String relationshipTypeName )
    {
        return new RelationshipTypeName( relationshipTypeName );
    }
}
