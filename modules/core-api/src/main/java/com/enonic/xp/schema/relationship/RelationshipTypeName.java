package com.enonic.xp.schema.relationship;

import com.google.common.annotations.Beta;

import com.enonic.xp.module.ModuleKey;
import com.enonic.xp.schema.BaseSchemaName;

@Beta
public final class RelationshipTypeName
    extends BaseSchemaName
{
    public static final RelationshipTypeName REFERENCE = new RelationshipTypeName( ModuleKey.SYSTEM, "reference" );

    public static final RelationshipTypeName PARENT = new RelationshipTypeName( ModuleKey.SYSTEM, "parent" );

    private RelationshipTypeName( final String name )
    {
        super( name );
    }

    private RelationshipTypeName( final ModuleKey moduleKey, final String localName )
    {
        super( moduleKey, localName );
    }

    public static RelationshipTypeName from( final ModuleKey moduleKey, final String localName )
    {
        return new RelationshipTypeName( moduleKey, localName );
    }

    public static RelationshipTypeName from( final String relationshipTypeName )
    {
        return new RelationshipTypeName( relationshipTypeName );
    }
}
