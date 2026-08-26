package com.enonic.xp.core.impl.app;

import java.io.IOException;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import com.enonic.xp.app.ApplicationDescriptor;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationType;
import com.enonic.xp.core.impl.schema.YmlParserBase;
import com.enonic.xp.schema.LocalizedText;
import com.enonic.xp.util.GenericValue;

final class YmlApplicationDescriptorParser
{
    private static final YmlParserBase PARSER = new YmlParserBase();

    static
    {
        PARSER.addMixIn( ApplicationDescriptor.Builder.class, ApplicationDescriptorBuilderMapper.class );
    }

    static ApplicationDescriptor.Builder parse( final String resource, final ApplicationKey currentApplication )
    {
        return PARSER.parse( "Application", resource, ApplicationDescriptor.Builder.class, currentApplication );
    }

    @JsonIgnoreProperties("kind")
    private abstract static class ApplicationDescriptorBuilderMapper
    {
        @JsonProperty("type")
        @JsonDeserialize(using = ApplicationTypeDeserializer.class)
        abstract ApplicationDescriptor.Builder type( ApplicationType type );

        @JsonProperty("title")
        abstract ApplicationDescriptor.Builder title( LocalizedText text );

        @JsonProperty("description")
        abstract ApplicationDescriptor.Builder description( LocalizedText text );

        @JsonProperty("vendorName")
        abstract ApplicationDescriptor.Builder vendorName( String value );

        @JsonProperty("vendorUrl")
        abstract ApplicationDescriptor.Builder vendorUrl( String value );

        @JsonProperty("url")
        abstract ApplicationDescriptor.Builder url( String value );

        @JacksonInject("currentApplication")
        abstract ApplicationDescriptor.Builder key( ApplicationKey key );

        @JsonProperty("config")
        abstract ApplicationDescriptor.Builder schemaConfig( GenericValue schemaConfig );
    }

    private static class ApplicationTypeDeserializer
        extends JsonDeserializer<ApplicationType>
    {
        @Override
        public ApplicationType deserialize( final JsonParser parser, final DeserializationContext context )
            throws IOException
        {
            final String value = parser.getValueAsString();
            return switch ( value )
            {
                case "Static" -> ApplicationType.STATIC;
                case "Bundle" -> ApplicationType.BUNDLE;
                default -> throw new IllegalArgumentException( String.format( "Unknown application type \"%s\"", value ) );
            };
        }
    }
}
