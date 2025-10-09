package com.enonic.xp.core.impl.content.parser;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.core.impl.schema.YmlParserBase;
import com.enonic.xp.form.Form;
import com.enonic.xp.schema.xdata.XDataName;
import com.enonic.xp.site.CmsDescriptor;
import com.enonic.xp.site.XDataMapping;
import com.enonic.xp.site.XDataMappings;

public class YmlCmsDescriptorParser
{
    private static final YmlParserBase PARSER = new YmlParserBase();

    static
    {
        PARSER.addMixIn( CmsDescriptor.Builder.class, CmsDescriptorBuilderMixIn.class );

        PARSER.addMixIn( XDataMapping.class, CmsDescriptorBuilderMixIn.XDataMappingMixIn.class );
        PARSER.addMixIn( XDataMapping.Builder.class, CmsDescriptorBuilderMixIn.XDataMappingMixIn.Builder.class );
    }

    public static CmsDescriptor.Builder parse( final String resource, final ApplicationKey currentApplication )
    {
        return PARSER.parse( resource, CmsDescriptor.Builder.class, currentApplication );
    }

    public abstract static class CmsDescriptorBuilderMixIn
    {
        @JsonCreator
        static CmsDescriptor.Builder create()
        {
            return CmsDescriptor.create();
        }

        @JsonProperty("x")
        @JsonDeserialize(using = XDataMappingsDeserializer.class)
        abstract CmsDescriptor.Builder xDataMappings( XDataMappings xDataMappings );

        @JsonProperty("form")
        abstract CmsDescriptor.Builder form( Form form );

        @JacksonInject("currentApplication")
        abstract CmsDescriptor.Builder applicationKey( ApplicationKey currentApplication );


        @JsonDeserialize(builder = XDataMapping.Builder.class)
        abstract static class XDataMappingMixIn
        {
            @JsonCreator
            static XDataMapping.Builder create()
            {
                return XDataMapping.create();
            }

            @JsonPOJOBuilder(withPrefix = "")
            abstract static class Builder
            {
                @JsonProperty("name")
                @JsonDeserialize(using = XDataNameDeserializer.class)
                abstract XDataMapping.Builder xDataName( XDataName xDataName );

                @JsonProperty("allowContentTypes")
                abstract XDataMapping.Builder allowContentTypes( String allowContentTypes );

                @JsonProperty("optional")
                abstract XDataMapping.Builder optional( Boolean optional );
            }
        }
    }

    private static final class XDataMappingsDeserializer
        extends JsonDeserializer<XDataMappings>
    {
        @Override
        public XDataMappings deserialize( final JsonParser jsonParser, final DeserializationContext ctxt )
            throws IOException
        {
            final ObjectMapper mapper = (ObjectMapper) jsonParser.getCodec();
            final List<XDataMapping> keys = mapper.readValue( jsonParser, new TypeReference<>()
            {
            } );
            return XDataMappings.from( keys );
        }
    }

    private static final class XDataNameDeserializer
        extends JsonDeserializer<XDataName>
    {
        @Override
        public XDataName deserialize( final JsonParser jsonParser, final DeserializationContext ctxt )
            throws IOException
        {
            final ObjectMapper mapper = (ObjectMapper) jsonParser.getCodec();
            final JsonNode node = mapper.readTree( jsonParser );

            final String rawValue = node.asText();
            if ( rawValue.contains( ":" ) )
            {
                return XDataName.from( rawValue );
            }
            else
            {
                final ApplicationKey currentApplication = (ApplicationKey) ctxt.findInjectableValue( "currentApplication", null, null );
                return XDataName.from( currentApplication, rawValue );
            }
        }
    }
}
