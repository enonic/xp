package com.enonic.xp.support;


import java.io.IOException;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import com.enonic.xp.json.ObjectMapperHelper;

import static org.junit.Assert.*;

public class JsonTestHelper
{
    private final ObjectMapper objectMapper;

    private final ObjectWriter objectWriter;

    private final ResourceTestHelper resourceTestHelper;

    public JsonTestHelper( final Object testInstance )
    {
        this.resourceTestHelper = new ResourceTestHelper( testInstance );
        objectMapper = ObjectMapperHelper.create();
        objectWriter = objectMapper.writerWithDefaultPrettyPrinter();
    }

    public ObjectMapper objectMapper()
    {
        return objectMapper;
    }

    public JsonNode loadTestJson( final String fileName )
    {
        return stringToJson( resourceTestHelper.loadTestFile( fileName ) );
    }

    public String jsonToString( final JsonNode value )
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


    public JsonNode stringToJson( final String jsonString )
    {
        try
        {
            final ObjectMapper mapper = objectMapper;
            final JsonFactory factory = mapper.getFactory();
            final JsonParser parser = factory.createParser( jsonString );
            return parser.readValueAsTree();
        }
        catch ( IOException e )
        {
            throw new RuntimeException( e );
        }
    }

    public void assertJsonEquals( final JsonNode expectedJson, final JsonNode actualJson )
    {
        assertEquals( jsonToString( expectedJson ), jsonToString( actualJson ) );
    }
}
