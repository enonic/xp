package com.enonic.xp.core.impl.app;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.app.ApplicationDescriptor;
import com.enonic.xp.app.ApplicationKey;
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
        @JsonProperty("title")
        abstract ApplicationDescriptor.Builder title( LocalizedText text );

        @JsonProperty("description")
        abstract ApplicationDescriptor.Builder description( LocalizedText text );

        @JsonProperty("vendorName")
        abstract ApplicationDescriptor.Builder vendorName( String value );

        @JsonProperty("vendorUrl")
        abstract ApplicationDescriptor.Builder vendorUrl( String value );

        @JacksonInject("currentApplication")
        abstract ApplicationDescriptor.Builder key( ApplicationKey key );

        @JsonProperty("config")
        abstract ApplicationDescriptor.Builder schemaConfig( GenericValue schemaConfig );
    }
}
