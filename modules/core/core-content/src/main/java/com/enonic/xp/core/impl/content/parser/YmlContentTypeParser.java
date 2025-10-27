package com.enonic.xp.core.impl.content.parser;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.core.impl.schema.YmlParserBase;
import com.enonic.xp.form.Form;
import com.enonic.xp.util.GenericValue;
import com.enonic.xp.schema.LocalizedText;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;

public final class YmlContentTypeParser
{
    private static final YmlParserBase PARSER = new YmlParserBase();

    static
    {
        PARSER.addMixIn( ContentTypeName.class, ContentTypeNameMixIn.class );
        PARSER.addMixIn( ContentType.Builder.class, ContentTypeBuilderMixIn.class );
    }

    public static ContentType.Builder parse( final String resource, final ApplicationKey currentApplication )
    {
        final ContentType.Builder builder = PARSER.parse( resource, ContentType.Builder.class, currentApplication );
        builder.name( "_TEMP_NAME_" );

        final ContentType contentType = builder.build();

        return ContentType.create( contentType )
            .superType( toContentTypeName( contentType.getSuperType().toString(), currentApplication ) );
    }

    private static ContentTypeName toContentTypeName( final String name, final ApplicationKey currentApplication )
    {

        if ( name.contains( ":" ) )
        {
            return ContentTypeName.from( name );
        }

        if ( currentApplication == null )
        {
            throw new IllegalArgumentException( String.format( "Unable to resolve application for ContentType [%s]", name ) );
        }

        return ContentTypeName.from( currentApplication, name );
    }

    private abstract static class ContentTypeNameMixIn
    {
        @JsonCreator
        public static ContentTypeName from( String value )
        {
            return ContentTypeName.from( value );
        }
    }

    private abstract static class ContentTypeBuilderMixIn
    {
        @JsonProperty("name")
        public abstract ContentType.Builder name( String name );

        @JsonProperty("form")
        public abstract ContentType.Builder form( Form form );

        @JsonProperty("superType")
        abstract ContentType.Builder superType( ContentTypeName name );

        @JsonProperty("displayName")
        public abstract ContentType.Builder setDisplayName( LocalizedText value );

        @JsonProperty("description")
        public abstract ContentType.Builder setDescription( LocalizedText value );

        @JsonProperty("abstract")
        abstract ContentType.Builder setAbstract( boolean value );

        @JsonProperty("final")
        abstract ContentType.Builder setFinal( boolean value );

        @JsonProperty(value = "allowChildContent", defaultValue = "true")
        abstract ContentType.Builder allowChildContent( boolean value );

        @JsonProperty("allowChildContentType")
        abstract ContentType.Builder allowChildContentType( List<String> allowChildContentType );

        @JsonProperty("config")
        abstract ContentType.Builder schemaConfig( GenericValue config );
    }

}
