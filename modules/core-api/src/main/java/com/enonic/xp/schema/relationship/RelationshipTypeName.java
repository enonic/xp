package com.enonic.xp.schema.relationship;


import org.apache.commons.lang.StringUtils;

import com.google.common.annotations.Beta;

import com.enonic.xp.module.ModuleBasedName;
import com.enonic.xp.module.ModuleKey;

@Beta
public final class RelationshipTypeName
    extends ModuleBasedName
{
    public static final RelationshipTypeName REFERENCE = new RelationshipTypeName( "reference" );

    public static final RelationshipTypeName PARENT = new RelationshipTypeName( "parent" );

    private RelationshipTypeName( final ModuleKey moduleKey, final String localName )
    {
        super( moduleKey, localName );
    }

    private RelationshipTypeName( final String localName )
    {
        super( ModuleKey.SYSTEM, localName );
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
