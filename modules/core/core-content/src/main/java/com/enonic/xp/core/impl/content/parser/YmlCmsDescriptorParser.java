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
import com.enonic.xp.schema.mixin.MixinName;
import com.enonic.xp.site.CmsDescriptor;
import com.enonic.xp.site.MixinMapping;
import com.enonic.xp.site.MixinMappings;

public class YmlCmsDescriptorParser
{
    private static final YmlParserBase PARSER = new YmlParserBase();

    static
    {
        PARSER.addMixIn( CmsDescriptor.Builder.class, CmsDescriptorBuilderMixIn.class );

        PARSER.addMixIn( MixinMapping.class, CmsDescriptorBuilderMixIn.MixinMappingMixIn.class );
        PARSER.addMixIn( MixinMapping.Builder.class, CmsDescriptorBuilderMixIn.MixinMappingMixIn.Builder.class );
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
        @JsonDeserialize(using = MixinMappingsDeserializer.class)
        abstract CmsDescriptor.Builder mixinMappings( MixinMappings mixinMappings );

        @JsonProperty("form")
        abstract CmsDescriptor.Builder form( Form form );

        @JacksonInject("currentApplication")
        abstract CmsDescriptor.Builder applicationKey( ApplicationKey currentApplication );


        @JsonDeserialize(builder = MixinMapping.Builder.class)
        abstract static class MixinMappingMixIn
        {
            @JsonCreator
            static MixinMapping.Builder create()
            {
                return MixinMapping.create();
            }

            @JsonPOJOBuilder(withPrefix = "")
            abstract static class Builder
            {
                @JsonProperty("name")
                @JsonDeserialize(using = MixinNameDeserializer.class)
                abstract MixinMapping.Builder mixinName( MixinName mixinName );

                @JsonProperty("allowContentTypes")
                abstract MixinMapping.Builder allowContentTypes( String allowContentTypes );

                @JsonProperty("optional")
                abstract MixinMapping.Builder optional( Boolean optional );
            }
        }
    }

    private static final class MixinMappingsDeserializer
        extends JsonDeserializer<MixinMappings>
    {
        @Override
        public MixinMappings deserialize( final JsonParser jsonParser, final DeserializationContext ctxt )
            throws IOException
        {
            final ObjectMapper mapper = (ObjectMapper) jsonParser.getCodec();
            final List<MixinMapping> keys = mapper.readValue( jsonParser, new TypeReference<>()
            {
            } );
            return MixinMappings.from( keys );
        }
    }

    private static final class MixinNameDeserializer
        extends JsonDeserializer<MixinName>
    {
        @Override
        public MixinName deserialize( final JsonParser jsonParser, final DeserializationContext ctxt )
            throws IOException
        {
            final ObjectMapper mapper = (ObjectMapper) jsonParser.getCodec();
            final JsonNode node = mapper.readTree( jsonParser );

            final String rawValue = node.asText();
            if ( rawValue.contains( ":" ) )
            {
                return MixinName.from( rawValue );
            }
            else
            {
                final ApplicationKey currentApplication = (ApplicationKey) ctxt.findInjectableValue( "currentApplication", null, null );
                return MixinName.from( currentApplication, rawValue );
            }
        }
    }
}
