package com.enonic.xp.support;


import java.io.IOException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;

import com.enonic.xp.core.internal.json.ObjectMapperHelper;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JsonTestHelper
{
    private static final ObjectMapper MAPPER = ObjectMapperHelper.create();

    private static final ObjectWriter OBJECT_WRITER = MAPPER.writerWithDefaultPrettyPrinter();

    private static final ObjectReader OBJECT_READER = MAPPER.reader();

    private final ResourceTestHelper resourceTestHelper;

    public JsonTestHelper( final Object testInstance )
    {
        this.resourceTestHelper = new ResourceTestHelper( testInstance );
    }

    public ObjectReader objectReader()
    {
        return OBJECT_READER;
    }

    public JsonNode loadTestJson( final String fileName )
    {
        return stringToJson( resourceTestHelper.loadTestFile( fileName ) );
    }

    public String objectToString( final Object value )
    {
        try
        {
            return OBJECT_WRITER.writeValueAsString( value );
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
            return MAPPER.valueToTree( value );
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
            return MAPPER.readTree( jsonString );
        }
        catch ( IOException e )
        {
            throw new RuntimeException( e );
        }
    }

    public JsonNode bytesToJson( final byte[] jsonBytes )
    {
        try
        {
            return MAPPER.readTree( jsonBytes );
        }
        catch ( IOException e )
        {
            throw new RuntimeException( e );
        }
    }

    public void assertJsonEquals( final JsonNode expectedJson, final JsonNode actualJson )
    {
        assertEquals( objectToString( expectedJson ), objectToString( actualJson ) );
    }
}
