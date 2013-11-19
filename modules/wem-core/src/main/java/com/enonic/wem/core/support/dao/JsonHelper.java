package com.enonic.wem.core.support.dao;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;

public class JsonHelper
{

    private final ObjectWriter objectWriter;

    private final ObjectMapper objectMapper;

    public JsonHelper()
    {
        objectMapper = new ObjectMapper();
        objectMapper.enable( MapperFeature.SORT_PROPERTIES_ALPHABETICALLY );
        objectMapper.disable( SerializationFeature.FAIL_ON_EMPTY_BEANS );
        objectWriter = objectMapper.writerWithDefaultPrettyPrinter();
    }


    public String objectToString( final Object value )
    {
        try
        {
            return objectWriter.writeValueAsString( value );
        }
        catch ( Exception e )
        {
            throw new RuntimeException( e );
        }
    }

    public JsonNode objectToJson( final Object value )
    {
        try
        {
            return objectMapper.valueToTree( value );
        }
        catch ( Exception e )
        {
            throw new RuntimeException( e );
        }
    }


    public ObjectMapper objectMapper()
    {
        return objectMapper;
    }

}
