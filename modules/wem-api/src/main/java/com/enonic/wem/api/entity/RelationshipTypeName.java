package com.enonic.wem.api.entity;


import com.enonic.wem.api.Name;

public final class RelationshipTypeName

{
    public static final RelationshipTypeName DEFAULT = new RelationshipTypeName( "default" );

    public static final RelationshipTypeName PARENT = new RelationshipTypeName( "parent" );

    public static final RelationshipTypeName LINK = new RelationshipTypeName( "link" );

    public static final RelationshipTypeName LIKE = new RelationshipTypeName( "like" );

    private final Name name;

    public RelationshipTypeName( final String name )
    {
        this.name = Name.from( name );
    }

    public RelationshipTypeName( final Name name )
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

    public static RelationshipTypeName from( final String value )
    {
        return new RelationshipTypeName( value );
    }
}
