package com.enonic.xp.core.impl.content.parser;

import java.io.IOException;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.core.impl.schema.YmlParserBase;
import com.enonic.xp.schema.LocalizedText;
import com.enonic.xp.style.GenericStyle;
import com.enonic.xp.style.ImageStyle;
import com.enonic.xp.style.StyleDescriptor;

final class YmlStyleDescriptorParser
{
    private static final YmlParserBase PARSER = new YmlParserBase();

    static
    {
        PARSER.addMixIn( StyleDescriptor.Builder.class, StyleDescriptorBuilderMixIn.class );

        PARSER.addMixIn( ImageStyle.class, ImageStyleMixIn.class );
        PARSER.addMixIn( ImageStyle.Builder.class, ImageStyleMixIn.Builder.class );

        PARSER.addMixIn( GenericStyle.class, GenericStyleMixIn.class );
        PARSER.addMixIn( GenericStyle.Builder.class, GenericStyleMixIn.Builder.class );
    }

    static StyleDescriptor.Builder parse( final String resource, final ApplicationKey currentApplication )
    {
        return PARSER.parse( resource, StyleDescriptor.Builder.class, currentApplication );
    }

    private abstract static class StyleDescriptorBuilderMixIn
    {
        @JsonCreator
        static StyleDescriptor.Builder create()
        {
            return StyleDescriptor.create();
        }

        @JsonProperty("css")
        abstract StyleDescriptor.Builder cssPath( String cssPath );

        @JsonProperty("image")
        @JsonDeserialize(using = ImageStyleDeserializer.class)
        abstract StyleDescriptor.Builder addImageStyles( Iterable<ImageStyle> elements );

        @JsonProperty("generic")
        @JsonDeserialize(using = GenericStyleDeserializer.class)
        abstract StyleDescriptor.Builder addGenericStyles( Iterable<GenericStyle> elements );
    }

    @JsonDeserialize(builder = ImageStyle.Builder.class)
    private abstract static class ImageStyleMixIn
    {
        @JsonCreator
        static ImageStyle.Builder create()
        {
            return ImageStyle.create();
        }

        @JsonPOJOBuilder(withPrefix = "")
        abstract static class Builder
        {
            @JsonProperty("name")
            abstract ImageStyle.Builder name( String name );

            @JsonProperty("displayName")
            abstract ImageStyle.Builder displayName( LocalizedText text );

            @JsonProperty("aspectRatio")
            abstract ImageStyle.Builder aspectRatio( String aspectRatio );

            @JsonProperty("filter")
            abstract ImageStyle.Builder filter( String filter );
        }
    }

    @JsonDeserialize(builder = GenericStyle.Builder.class)
    private abstract static class GenericStyleMixIn
    {
        @JsonCreator
        static GenericStyle.Builder create()
        {
            return GenericStyle.create();
        }

        @JsonPOJOBuilder(withPrefix = "")
        abstract static class Builder
        {
            @JsonProperty("name")
            abstract GenericStyle.Builder name( String name );

            @JsonProperty("displayName")
            abstract GenericStyle.Builder displayName( LocalizedText text );
        }
    }

    private static final class ImageStyleDeserializer
        extends JsonDeserializer<Iterable<ImageStyle>>
    {

        @Override
        public Iterable<ImageStyle> deserialize( final JsonParser jsonParser, final DeserializationContext ctxt )
            throws IOException
        {
            final ObjectMapper mapper = (ObjectMapper) jsonParser.getCodec();
            return mapper.readValue( jsonParser, new TypeReference<>()
            {
            } );
        }
    }

    private static final class GenericStyleDeserializer
        extends JsonDeserializer<Iterable<GenericStyle>>
    {

        @Override
        public Iterable<GenericStyle> deserialize( final JsonParser jsonParser, final DeserializationContext ctxt )
            throws IOException
        {
            final ObjectMapper mapper = (ObjectMapper) jsonParser.getCodec();
            return mapper.readValue( jsonParser, new TypeReference<>()
            {
            } );
        }
    }
}
