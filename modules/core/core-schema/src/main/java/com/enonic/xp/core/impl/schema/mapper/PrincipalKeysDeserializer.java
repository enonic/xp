package com.enonic.xp.core.impl.schema.mapper;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.JsonParser;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;


import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalKeys;

public class PrincipalKeysDeserializer
    extends JsonDeserializer<PrincipalKeys>
{
    @Override
    public PrincipalKeys deserialize( final JsonParser jsonParser, final DeserializationContext ctxt )
        throws IOException
    {
        final List<PrincipalKey> keys = ctxt.readValue( jsonParser,
                                                       ctxt.getTypeFactory().constructCollectionType( List.class, PrincipalKey.class ) );
        return PrincipalKeys.from( keys );
    }
}
