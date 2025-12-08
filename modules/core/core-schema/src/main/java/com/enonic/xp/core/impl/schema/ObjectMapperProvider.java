package com.enonic.xp.core.impl.schema;

import com.fasterxml.jackson.databind.ObjectMapper;

final class ObjectMapperProvider
{
    static ObjectMapper MAPPER = new ObjectMapper();

    private ObjectMapperProvider()
    {
        throw new AssertionError();
    }
}
