package com.enonic.xp.impl.server.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public final class ModelToStringHelper
{
    private static final ObjectMapper MAPPER = new ObjectMapper();

    static
    {
        MAPPER.registerModule( new JavaTimeModule() );
    }

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
