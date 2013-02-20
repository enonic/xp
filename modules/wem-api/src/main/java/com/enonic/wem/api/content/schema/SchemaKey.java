package com.enonic.wem.api.content.schema;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import com.enonic.wem.api.content.schema.content.QualifiedContentTypeName;
import com.enonic.wem.api.content.schema.mixin.QualifiedMixinName;
import com.enonic.wem.api.content.schema.relationshiptype.QualifiedRelationshipTypeName;
import com.enonic.wem.api.module.ModuleName;

public final class SchemaKey
{
    private final static char SEPARATOR = ':';

    private final static Pattern REF_PATTERN = Pattern.compile( "^([^:]+):([^:]+):([^:]+)$" );


    private final String refString;

    private final SchemaKind type;

    private final ModuleName moduleName;

    private final String localName;

    private SchemaKey( final SchemaKind type, final ModuleName moduleName, final String localName )
    {
        this.type = type;
        this.moduleName = moduleName;
        this.localName = localName;
        this.refString = Joiner.on( SEPARATOR ).join( this.type, this.moduleName, this.localName );
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

    public ModuleName getModuleName()
    {
        return this.moduleName;
    }

    public String getLocalName()
    {
        return localName;
    }

    public String toString()
    {
        return refString;
    }

    public static SchemaKey from( final QualifiedContentTypeName contentTypeName )
    {
        return new SchemaKey( SchemaKind.CONTENT_TYPE, contentTypeName.getModuleName(), contentTypeName.getLocalName() );
    }

    public static SchemaKey from( final QualifiedMixinName mixinName )
    {
        return new SchemaKey( SchemaKind.MIXIN, mixinName.getModuleName(), mixinName.getLocalName() );
    }

    public static SchemaKey from( final QualifiedRelationshipTypeName relationshipTypeName )
    {
        return new SchemaKey( SchemaKind.RELATIONSHIP_TYPE, relationshipTypeName.getModuleName(), relationshipTypeName.getLocalName() );
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
        final ModuleName moduleName = ModuleName.from( matcher.group( 2 ) );
        final String localName = matcher.group( 3 );

        final SchemaKind typeKind = SchemaKind.from( type );
        if ( typeKind == null )
        {
            throw new IllegalArgumentException( "Not a valid SchemaKey [" + value + "]" );
        }
        return new SchemaKey( typeKind, moduleName, localName );
    }

}
