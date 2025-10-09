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
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.core.impl.schema.YmlParserBase;
import com.enonic.xp.descriptor.DescriptorKeys;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.site.SiteDescriptor;
import com.enonic.xp.site.mapping.ControllerMappingDescriptor;
import com.enonic.xp.site.mapping.ControllerMappingDescriptors;
import com.enonic.xp.site.processor.ResponseProcessorDescriptor;
import com.enonic.xp.site.processor.ResponseProcessorDescriptors;

public final class YmlSiteDescriptorParser
{
    private static final YmlParserBase PARSER = new YmlParserBase();

    static
    {
        PARSER.addMixIn( SiteDescriptor.Builder.class, SiteDescriptorBuilderMixIn.class );

        PARSER.addMixIn( ResponseProcessorDescriptor.class, SiteDescriptorBuilderMixIn.ResponseProcessorDescriptorMixIn.class );
        PARSER.addMixIn( ResponseProcessorDescriptor.Builder.class,
                         SiteDescriptorBuilderMixIn.ResponseProcessorDescriptorMixIn.Builder.class );

        PARSER.addMixIn( ControllerMappingDescriptor.class, SiteDescriptorBuilderMixIn.ControllerMappingDescriptorMixIn.class );
        PARSER.addMixIn( ControllerMappingDescriptor.Builder.class,
                         SiteDescriptorBuilderMixIn.ControllerMappingDescriptorMixIn.Builder.class );
    }

    public static SiteDescriptor.Builder parse( final String resource, final ApplicationKey currentApplication )
    {
        return PARSER.parse( resource, SiteDescriptor.Builder.class, currentApplication );
    }

    abstract static class SiteDescriptorBuilderMixIn
    {
        @JsonCreator
        static SiteDescriptor.Builder create()
        {
            return SiteDescriptor.create();
        }

        @JsonProperty("processors")
        @JsonDeserialize(using = ResponseProcessorDescriptorsDeserializer.class)
        abstract SiteDescriptor.Builder responseProcessors( ResponseProcessorDescriptors responseProcessors );

        @JsonProperty("mappings")
        @JsonDeserialize(using = ControllerMappingDescriptorsDeserializer.class)
        abstract SiteDescriptor.Builder mappingDescriptors( ControllerMappingDescriptors mappingDescriptors );

        @JsonProperty("apis")
        abstract SiteDescriptor.Builder apiMounts( DescriptorKeys apiMounts );

        @JacksonInject("currentApplication")
        abstract SiteDescriptor.Builder applicationKey( ApplicationKey applicationKey );

        @JsonDeserialize(builder = ResponseProcessorDescriptor.Builder.class)
        abstract static class ResponseProcessorDescriptorMixIn
        {
            @JsonCreator
            static ResponseProcessorDescriptor.Builder create()
            {
                return ResponseProcessorDescriptor.create();
            }

            @JsonPOJOBuilder(withPrefix = "")
            abstract static class Builder
            {
                @JsonProperty("name")
                abstract ResponseProcessorDescriptor.Builder name( String name );

                @JsonProperty("order")
                abstract ResponseProcessorDescriptor.Builder order( int order );

                @JacksonInject("currentApplication")
                abstract ResponseProcessorDescriptor.Builder application( ApplicationKey application );
            }
        }

        @JsonDeserialize(builder = ControllerMappingDescriptor.Builder.class)
        abstract static class ControllerMappingDescriptorMixIn
        {
            @JsonCreator
            static ControllerMappingDescriptor.Builder create()
            {
                return ControllerMappingDescriptor.create();
            }

            @JsonPOJOBuilder(withPrefix = "")
            abstract static class Builder
            {
                @JsonProperty("service")
                abstract ControllerMappingDescriptor.Builder service( String service );

                @JsonProperty("controller")
                abstract ControllerMappingDescriptor.Builder controller( ResourceKey controller );

                @JsonProperty("filter")
                abstract ControllerMappingDescriptor.Builder filter( ResourceKey filter );

                @JsonProperty("pattern")
                abstract ControllerMappingDescriptor.Builder pattern( String pattern );

                @JsonProperty("invertPattern")
                abstract ControllerMappingDescriptor.Builder invertPattern( boolean invertPattern );

                @JsonProperty("order")
                abstract ControllerMappingDescriptor.Builder order( int order );

                @JsonProperty("match")
                abstract ControllerMappingDescriptor.Builder contentConstraint( String contentConstraint );
            }
        }

        private static final class ResponseProcessorDescriptorsDeserializer
            extends JsonDeserializer<ResponseProcessorDescriptors>
        {

            @Override
            public ResponseProcessorDescriptors deserialize( final JsonParser jsonParser, final DeserializationContext ctxt )
                throws IOException
            {
                final ObjectMapper mapper = (ObjectMapper) jsonParser.getCodec();
                final List<ResponseProcessorDescriptor> keys = mapper.readValue( jsonParser, new TypeReference<>()
                {
                } );
                return ResponseProcessorDescriptors.from( keys );
            }
        }

        private static final class ControllerMappingDescriptorsDeserializer
            extends JsonDeserializer<ControllerMappingDescriptors>
        {

            @Override
            public ControllerMappingDescriptors deserialize( final JsonParser jsonParser, final DeserializationContext ctxt )
                throws IOException
            {
                final ObjectMapper mapper = (ObjectMapper) jsonParser.getCodec();
                final List<ControllerMappingDescriptor> keys = mapper.readValue( jsonParser, new TypeReference<>()
                {
                } );
                return ControllerMappingDescriptors.from( keys );
            }
        }
    }
}
