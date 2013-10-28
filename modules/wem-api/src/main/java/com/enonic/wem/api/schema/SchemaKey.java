package com.enonic.wem.api.schema;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.mixin.MixinName;
import com.enonic.wem.api.schema.relationship.RelationshipTypeName;

public final class SchemaKey
{
    private final static char SEPARATOR = ':';

    private final static Pattern REF_PATTERN = Pattern.compile( "^([^:]+):([^:]+)$" );


    private final String refString;

    private final SchemaKind type;

    private final String localName;

    private SchemaKey( final SchemaKind type, final String localName )
    {
        this.type = type;
        this.localName = localName;
        this.refString = Joiner.on( SEPARATOR ).join( this.type, this.localName );
    }

    public boolean isContentType()
    {
        return this.type == SchemaKind.CONTENT_TYPE;
    }

    public boolean isMixin()
    {
        return this.type == SchemaKind.MIXIN;
    }

    public boolean isRelationshipType()
    {
        return this.type == SchemaKind.RELATIONSHIP_TYPE;
    }

    public String getLocalName()
    {
        return localName;
    }

    public String toString()
    {
        return refString;
    }

    public static SchemaKey from( final ContentTypeName contentTypeName )
    {
        return new SchemaKey( SchemaKind.CONTENT_TYPE, contentTypeName.toString() );
    }

    public static SchemaKey from( final MixinName mixinName )
    {
        return new SchemaKey( SchemaKind.MIXIN, mixinName.toString() );
    }

    public static SchemaKey from( final RelationshipTypeName relationshipTypeName )
    {
        return new SchemaKey( SchemaKind.RELATIONSHIP_TYPE, relationshipTypeName.toString() );
    }

    public static SchemaKey from( final String value )
    {
        Preconditions.checkArgument( !Strings.isNullOrEmpty( value ), "SchemaKey cannot be null or empty" );

        final Matcher matcher = REF_PATTERN.matcher( value );
        if ( !matcher.find() )
        {
            throw new IllegalArgumentException( "Not a valid SchemaKey [" + value + "]" );
        }

        final String type = matcher.group( 1 );
        final String name = matcher.group( 2 );

        final SchemaKind typeKind = SchemaKind.from( type );
        if ( typeKind == null )
        {
            throw new IllegalArgumentException( "Not a valid SchemaKey [" + value + "]" );
        }
        return new SchemaKey( typeKind, name );
    }

}
