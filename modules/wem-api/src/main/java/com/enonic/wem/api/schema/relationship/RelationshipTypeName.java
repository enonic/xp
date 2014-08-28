package com.enonic.wem.api.schema.relationship;


import org.apache.commons.lang.StringUtils;

import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.schema.SchemaKey;
import com.enonic.wem.api.schema.SchemaName;

public final class RelationshipTypeName
    extends SchemaName
{
    public static final RelationshipTypeName DEFAULT = new RelationshipTypeName( "default" );

    public static final RelationshipTypeName PARENT = new RelationshipTypeName( "parent" );

    public static final RelationshipTypeName LINK = new RelationshipTypeName( "link" );

    public static final RelationshipTypeName LIKE = new RelationshipTypeName( "like" );

    private RelationshipTypeName( final ModuleKey moduleKey, final String localName )
    {
        super( moduleKey, localName );
    }

    private RelationshipTypeName( final String localName )
    {
        super( ModuleKey.SYSTEM, localName );
    }

    @Override
    public SchemaKey toSchemaKey()
    {
        return SchemaKey.from( this );
    }

    public static RelationshipTypeName from( final ModuleKey moduleKey, final String localName )
    {
        return new RelationshipTypeName( moduleKey, localName );
    }

    public static RelationshipTypeName from( final String relationshipTypeName )
    {
        final String moduleKey = StringUtils.substringBefore( relationshipTypeName, SEPARATOR );
        final String localName = StringUtils.substringAfter( relationshipTypeName, SEPARATOR );
        return new RelationshipTypeName( ModuleKey.from( moduleKey ), localName );
    }
}
