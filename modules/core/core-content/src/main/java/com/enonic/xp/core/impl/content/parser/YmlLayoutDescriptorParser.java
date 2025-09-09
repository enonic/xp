package com.enonic.xp.core.impl.content.parser;

import java.io.IOException;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.core.impl.schema.YmlParserBase;
import com.enonic.xp.form.Form;
import com.enonic.xp.inputtype.InputTypeConfig;
import com.enonic.xp.inputtype.InputTypeProperty;
import com.enonic.xp.region.LayoutDescriptor;
import com.enonic.xp.region.RegionDescriptor;
import com.enonic.xp.region.RegionDescriptors;
import com.enonic.xp.schema.LocalizedText;

final class YmlLayoutDescriptorParser
{
    private static final YmlParserBase PARSER = new YmlParserBase();

    static
    {
        PARSER.addMixIn( LayoutDescriptor.Builder.class, LayoutDescriptorBuilderMixIn.class );
    }

    static LayoutDescriptor.Builder parse( final String resource, final ApplicationKey currentApplication )
    {
        return PARSER.parse( resource, LayoutDescriptor.Builder.class, currentApplication );
    }

    private abstract static class LayoutDescriptorBuilderMixIn
    {
        @JsonCreator
        static LayoutDescriptor.Builder create()
        {
            return LayoutDescriptor.create();
        }

        @JsonProperty("displayName")
        abstract LayoutDescriptor.Builder displayName( LocalizedText text );

        @JsonProperty("description")
        abstract LayoutDescriptor.Builder description( LocalizedText text );

        @JsonProperty("form")
        abstract LayoutDescriptor.Builder config( Form config );

        @JsonProperty("regions")
        @JsonDeserialize(using = RegionDescriptorsDeserializer.class)
        abstract LayoutDescriptor.Builder regions( RegionDescriptors value );

        @JsonProperty("config")
        @JsonDeserialize(using = InputTypeConfigDeserializer.class)
        abstract LayoutDescriptor.Builder schemaConfig( InputTypeConfig value );
    }

    private static final class RegionDescriptorsDeserializer
        extends JsonDeserializer<RegionDescriptors>
    {
        @Override
        public RegionDescriptors deserialize( final JsonParser jsonParser, final DeserializationContext ctxt )
            throws IOException
        {
            final ObjectMapper mapper = (ObjectMapper) jsonParser.getCodec();
            final JsonNode node = mapper.readTree( jsonParser );

            final RegionDescriptors.Builder builder = RegionDescriptors.create();

            for ( JsonNode region : node )
            {
                builder.add( RegionDescriptor.create().name( region.asText() ).build() );
            }

            return builder.build();
        }
    }

    private static final class InputTypeConfigDeserializer
        extends JsonDeserializer<InputTypeConfig>
    {
        @Override
        public InputTypeConfig deserialize( final JsonParser jsonParser, final DeserializationContext ctxt )
            throws IOException
        {
            final ObjectMapper mapper = (ObjectMapper) jsonParser.getCodec();
            final JsonNode node = mapper.readTree( jsonParser );

            final InputTypeConfig.Builder builder = InputTypeConfig.create();

            for ( JsonNode property : node )
            {
                final String name = property.get( "name" ).asText();
                final String value = property.get( "value" ).asText();

                final InputTypeProperty.Builder propertyBuilder = InputTypeProperty.create( name, value );

                property.fieldNames().forEachRemaining( attr -> {
                    if ( "value".equals( attr ) || "name".equals( attr ) )
                    {
                        return;
                    }
                    propertyBuilder.attribute( attr, property.get( attr ).asText() );
                } );

                builder.property( propertyBuilder.build() );
            }

            return builder.build();
        }
    }
}
