package com.enonic.xp.core.impl.schema.mapper;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.descriptor.DescriptorKeys;

public class DescriptorKeysDeserializer
    extends JsonDeserializer<DescriptorKeys>
{
    @Override
    public DescriptorKeys deserialize( final JsonParser jsonParser, final DeserializationContext ctxt )
        throws IOException
    {
        final ObjectMapper mapper = (ObjectMapper) jsonParser.getCodec();
        final List<DescriptorKey> keys = mapper.readValue( jsonParser, new TypeReference<>()
        {
        } );
        return DescriptorKeys.from( keys );
    }
}
