package com.enonic.xp.app.system.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.enonic.xp.core.internal.json.ObjectMapperHelper;

final class JsonHelper
{
    private static final ObjectMapper MAPPER = ObjectMapperHelper.create();

    private JsonHelper()
    {
    }

    static String toJson( final Object object )
    {
        try
        {
            return MAPPER.writeValueAsString( object );
        }
        catch ( JsonProcessingException e )
        {
            throw new RuntimeException( e );
        }
    }
}
