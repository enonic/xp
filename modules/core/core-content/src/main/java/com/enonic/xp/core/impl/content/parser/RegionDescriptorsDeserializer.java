package com.enonic.xp.core.impl.content.parser;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.enonic.xp.region.RegionDescriptor;
import com.enonic.xp.region.RegionDescriptors;

final class RegionDescriptorsDeserializer
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
