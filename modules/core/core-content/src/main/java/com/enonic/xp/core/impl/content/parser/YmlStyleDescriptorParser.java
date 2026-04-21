package com.enonic.xp.core.impl.content.parser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.core.impl.schema.YmlParserBase;
import com.enonic.xp.schema.LocalizedText;
import com.enonic.xp.style.ImageStyle;
import com.enonic.xp.style.Style;
import com.enonic.xp.style.StyleDescriptor;
import com.enonic.xp.util.GenericValue;

public final class YmlStyleDescriptorParser
{
    private static final YmlParserBase PARSER = new YmlParserBase();

    static
    {
        PARSER.addMixIn( StyleDescriptor.Builder.class, StyleDescriptorBuilderMixIn.class );

        PARSER.addMixIn( ImageStyle.class, ImageStyleMixIn.class );
        PARSER.addMixIn( ImageStyle.Builder.class, ImageStyleMixIn.Builder.class );
    }

    public static StyleDescriptor.Builder parse( final String resource, final ApplicationKey currentApplication )
    {
        return PARSER.parse( "Style", resource, StyleDescriptor.Builder.class, currentApplication );
    }

    @JsonIgnoreProperties("kind")
    private abstract static class StyleDescriptorBuilderMixIn
    {
        @JacksonInject("currentApplication")
        abstract StyleDescriptor.Builder application( ApplicationKey applicationKey );

        @JsonProperty("styles")
        @JsonDeserialize(using = StyleElementDeserializer.class)
        abstract StyleDescriptor.Builder addStyleElements( Iterable<? extends Style> elements );
    }

    @JsonDeserialize(builder = ImageStyle.Builder.class)
    private abstract static class ImageStyleMixIn
    {
        @JsonIgnoreProperties("type")
        @JsonPOJOBuilder(withPrefix = "")
        abstract static class Builder
        {
            @JsonProperty("name")
            abstract ImageStyle.Builder name( String name );

            @JsonProperty("label")
            abstract ImageStyle.Builder label( LocalizedText text );

            @JsonProperty("aspectRatio")
            abstract ImageStyle.Builder aspectRatio( String aspectRatio );

            @JsonProperty("filter")
            abstract ImageStyle.Builder filter( String filter );

            @JsonProperty("editor")
            abstract ImageStyle.Builder editor( GenericValue value );
        }
    }

    private static final class StyleElementDeserializer
        extends JsonDeserializer<Iterable<Style>>
    {
        @Override
        public Iterable<Style> deserialize( final JsonParser jsonParser, final DeserializationContext ctxt )
            throws IOException
        {
            final ObjectMapper mapper = (ObjectMapper) jsonParser.getCodec();
            final JsonNode node = mapper.readTree( jsonParser );

            final List<Style> result = new ArrayList<>();

            for ( JsonNode styleNode : node )
            {
                final String type = styleNode.path( "type" ).asText( null );

                if ( type == null )
                {
                    throw new IllegalArgumentException( "Style type can not be null" );
                }

                if ( "Image".equals( type ) )
                {
                    result.add( mapper.treeToValue( styleNode, ImageStyle.class ) );
                }
                else
                {
                    throw new IllegalArgumentException( String.format( "Unknown style type \"%s\"", type ) );
                }
            }

            return result;
        }
    }
}
