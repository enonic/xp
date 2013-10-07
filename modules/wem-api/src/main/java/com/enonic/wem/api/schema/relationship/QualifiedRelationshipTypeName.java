package com.enonic.wem.api.schema.relationship;


import com.enonic.wem.api.content.QualifiedName;

public final class QualifiedRelationshipTypeName
    extends QualifiedName
{
    public static final QualifiedRelationshipTypeName DEFAULT = new QualifiedRelationshipTypeName( "default" );

    public static final QualifiedRelationshipTypeName PARENT = new QualifiedRelationshipTypeName( "parent" );

    public static final QualifiedRelationshipTypeName LINK = new QualifiedRelationshipTypeName( "link" );

    public static final QualifiedRelationshipTypeName LIKE = new QualifiedRelationshipTypeName( "like" );

    private QualifiedRelationshipTypeName( final String name )
    {
        super( name );
    }

    public static QualifiedRelationshipTypeName from( String relationTypeName )
    {
        return new QualifiedRelationshipTypeName( relationTypeName );
    }
}
