package com.enonic.xp.core.impl.schema.mapper;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.descriptor.DescriptorKey;

public class DescriptorKeyDeserializer
    extends JsonDeserializer<DescriptorKey>
{
    @Override
    public DescriptorKey deserialize( final JsonParser jsonParser, final DeserializationContext ctxt )
        throws IOException
    {
        final ObjectMapper mapper = (ObjectMapper) jsonParser.getCodec();
        final JsonNode node = mapper.readTree( jsonParser );

        final String rawValue = node.asText();
        if ( rawValue.contains( ":" ) )
        {
            return DescriptorKey.from( rawValue );
        }
        else
        {
            final ApplicationKey currentApplication = (ApplicationKey) ctxt.findInjectableValue( "currentApplication", null, null );
            return DescriptorKey.from( currentApplication, rawValue );
        }
    }
}
