package com.enonic.xp.impl.server.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public final class ModelToStringHelper
{
    private static final ObjectMapper MAPPER = new ObjectMapper();

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
