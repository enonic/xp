package com.enonic.wem.api.schema.relationship;


import com.enonic.wem.api.schema.SchemaName;

public final class RelationshipTypeName
    extends SchemaName
{
    public static final RelationshipTypeName DEFAULT = new RelationshipTypeName( "default" );

    public static final RelationshipTypeName PARENT = new RelationshipTypeName( "parent" );

    public static final RelationshipTypeName LINK = new RelationshipTypeName( "link" );

    public static final RelationshipTypeName LIKE = new RelationshipTypeName( "like" );

    private RelationshipTypeName( final String name )
    {
        super( name );
    }

    public static RelationshipTypeName from( String relationTypeName )
    {
        return new RelationshipTypeName( relationTypeName );
    }
}
