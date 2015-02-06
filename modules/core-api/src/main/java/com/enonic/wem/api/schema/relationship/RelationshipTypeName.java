package com.enonic.wem.api.schema.relationship;


import org.apache.commons.lang.StringUtils;

import com.enonic.wem.api.module.ModuleBasedName;
import com.enonic.wem.api.module.ModuleKey;

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
