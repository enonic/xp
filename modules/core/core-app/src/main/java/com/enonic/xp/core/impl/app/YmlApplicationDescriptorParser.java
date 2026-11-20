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
import com.enonic.xp.core.impl.schema.YmlParserBase;
import com.enonic.xp.schema.LocalizedText;
import com.enonic.xp.util.GenericValue;

final class YmlApplicationDescriptorParser
{
    private static final String CURRENT_APPLICATION = "currentApplication";

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

        @JsonProperty("version")
        @JsonDeserialize(using = VersionDeserializer.class)
        abstract ApplicationDescriptor.Builder version( String value );

        @JsonProperty("minSystemVersion")
        @JsonDeserialize(using = VersionDeserializer.class)
        abstract ApplicationDescriptor.Builder minSystemVersion( String value );

        @JsonProperty("maxSystemVersion")
        @JsonDeserialize(using = VersionDeserializer.class)
        abstract ApplicationDescriptor.Builder maxSystemVersion( String value );

        @JsonProperty("name")
        @JacksonInject(CURRENT_APPLICATION)
        @JsonDeserialize(using = ApplicationNameDeserializer.class)
        abstract ApplicationDescriptor.Builder key( ApplicationKey key );

        @JsonProperty("config")
        abstract ApplicationDescriptor.Builder schemaConfig( GenericValue schemaConfig );
    }

    /**
     * The descriptor file declares a semantic version ({@code 1.2.3-SNAPSHOT}), the descriptor holds it the way the platform
     * reports versions, as the version of a bundle ({@code 1.2.3.SNAPSHOT}).
     */
    private static class VersionDeserializer
        extends JsonDeserializer<String>
    {
        @Override
        public String deserialize( final JsonParser parser, final DeserializationContext context )
            throws IOException
        {
            final String value = parser.getValueAsString();
            return value == null || value.isEmpty() ? value : ApplicationHelper.parseVersion( parser.currentName(), value ).toString();
        }
    }

    private static class ApplicationNameDeserializer
        extends JsonDeserializer<ApplicationKey>
    {
        @Override
        public ApplicationKey deserialize( final JsonParser parser, final DeserializationContext context )
            throws IOException
        {
            final ApplicationKey name = ApplicationKey.from( parser.getValueAsString() );
            final ApplicationKey currentApplication = (ApplicationKey) context.findInjectableValue( CURRENT_APPLICATION, null, null );

            if ( !name.equals( currentApplication ) )
            {
                throw new IllegalArgumentException(
                    String.format( "Application name \"%s\" in descriptor does not match application \"%s\"", name, currentApplication ) );
            }
            return currentApplication;
        }
    }
}