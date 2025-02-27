package com.enonic.xp.impl.server.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.enonic.xp.core.internal.json.ObjectMapperHelper;

public final class ModelToStringHelper
{
    private static final ObjectMapper MAPPER = ObjectMapperHelper.create();

    private ModelToStringHelper()
    {
    }

    public static String convertToString( Object object )
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
