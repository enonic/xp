package com.enonic.wem.api.entity;


import com.enonic.wem.api.Name;

public final class QualifiedRelationshipTypeName

{
    public static final QualifiedRelationshipTypeName DEFAULT = new QualifiedRelationshipTypeName( "default" );

    public static final QualifiedRelationshipTypeName PARENT = new QualifiedRelationshipTypeName( "parent" );

    public static final QualifiedRelationshipTypeName LINK = new QualifiedRelationshipTypeName( "link" );

    public static final QualifiedRelationshipTypeName LIKE = new QualifiedRelationshipTypeName( "like" );

    private final Name name;

    public QualifiedRelationshipTypeName( final String name )
    {
        this.name = Name.from( name );
    }

    public QualifiedRelationshipTypeName( final Name name )
    {
        this.name = name;
    }

    public Name getName()
    {
        return name;
    }

    @Override
    public String toString()
    {
        return this.name.toString();
    }

    public static QualifiedRelationshipTypeName from( final String value )
    {
        return new QualifiedRelationshipTypeName( value );
    }
}
