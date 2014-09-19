package com.enonic.wem.api.schema;


import org.apache.commons.lang.StringUtils;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import com.enonic.wem.api.schema.metadata.MetadataSchemaName;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.mixin.MixinName;
import com.enonic.wem.api.schema.relationship.RelationshipTypeName;

public final class SchemaKey
{
    private final static String SEPARATOR = ":";


    private final String refString;

    private final SchemaKind type;

    private final SchemaName name;

    private SchemaKey( final SchemaKind type, final SchemaName name )
    {
        this.type = type;
        this.name = name;
        this.refString = Joiner.on( SEPARATOR ).join( this.type, this.name.toString() );
    }

    public SchemaKind getType()
    {
        return type;
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

    public boolean isMetadataSchema()
    {
        return this.type == SchemaKind.METADATA_SCHEMA;
    }

    public SchemaName getName()
    {
        return this.name;
    }

    public String getLocalName()
    {
        return name.getLocalName();
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }

        final SchemaKey schemaKey = (SchemaKey) o;

        return refString.equals( schemaKey.refString );

    }

    @Override
    public int hashCode()
    {
        return refString.hashCode();
    }

    public String toString()
    {
        return refString;
    }

    public static SchemaKey from( final ContentTypeName contentTypeName )
    {
        return new SchemaKey( SchemaKind.CONTENT_TYPE, contentTypeName );
    }

    public static SchemaKey from( final MixinName mixinName )
    {
        return new SchemaKey( SchemaKind.MIXIN, mixinName );
    }

    public static SchemaKey from( final RelationshipTypeName relationshipTypeName )
    {
        return new SchemaKey( SchemaKind.RELATIONSHIP_TYPE, relationshipTypeName );
    }

    public static SchemaKey from( final MetadataSchemaName metadataSchemaName )
    {
        return new SchemaKey( SchemaKind.METADATA_SCHEMA, metadataSchemaName );
    }

    public static SchemaKey from( final SchemaKind type, final SchemaName name )
    {
        return new SchemaKey( type, name );
    }

    public static SchemaKey from( final String value )
    {
        Preconditions.checkArgument( !Strings.isNullOrEmpty( value ), "SchemaKey cannot be null or empty" );
        if ( !value.contains( SEPARATOR ) )
        {
            throw new IllegalArgumentException( "Not a valid SchemaKey [" + value + "]" );
        }

        final String type = StringUtils.substringBefore( value, SEPARATOR );
        final String name = StringUtils.substringAfter( value, SEPARATOR );

        final SchemaKind typeKind = SchemaKind.from( type );
        if ( typeKind == null )
        {
            throw new IllegalArgumentException( "Not a valid SchemaKey [" + value + "]" );
        }
        switch ( typeKind )
        {
            case CONTENT_TYPE:
                return new SchemaKey( typeKind, ContentTypeName.from( name ) );
            case MIXIN:
                return new SchemaKey( typeKind, MixinName.from( name ) );
            case RELATIONSHIP_TYPE:
                return new SchemaKey( typeKind, RelationshipTypeName.from( name ) );
            case METADATA_SCHEMA:
                return new SchemaKey( typeKind, MetadataSchemaName.from( name ) );
            default:
                throw new IllegalArgumentException( "Not a valid SchemaKey [" + value + "]" );
        }
    }

}
