package com.enonic.xp.core.impl.schema.mapper;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalKeys;

public class PrincipalKeysDeserializer
    extends JsonDeserializer<PrincipalKeys>
{
    @Override
    public PrincipalKeys deserialize( final JsonParser jsonParser, final DeserializationContext ctxt )
        throws IOException
    {
        final ObjectMapper mapper = (ObjectMapper) jsonParser.getCodec();
        final List<PrincipalKey> keys = mapper.readValue( jsonParser, new TypeReference<>()
        {
        } );
        return PrincipalKeys.from( keys );
    }
}
