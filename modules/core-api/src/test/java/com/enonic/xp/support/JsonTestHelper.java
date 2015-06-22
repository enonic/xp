package com.enonic.xp.support;


import java.io.IOException;
import java.text.SimpleDateFormat;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;

import static org.junit.Assert.*;

public class JsonTestHelper
{
    private final ObjectMapper objectMapper;

    private final ObjectWriter objectWriter;

    private final boolean prettyPrint;

    private final ResourceTestHelper resourceTestHelper;

    public JsonTestHelper()
    {
        this.resourceTestHelper = new ResourceTestHelper( this );
        this.prettyPrint = true;
        objectMapper = new ObjectMapper();
        objectMapper.setDateFormat( new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" ) );
        objectMapper.disable( SerializationFeature.WRITE_DATES_AS_TIMESTAMPS );
        objectMapper.disable( SerializationFeature.FAIL_ON_EMPTY_BEANS );
        objectMapper.enable( MapperFeature.SORT_PROPERTIES_ALPHABETICALLY );
        objectMapper.enable( SerializationFeature.WRITE_NULL_MAP_VALUES );
        objectMapper.setSerializationInclusion( JsonInclude.Include.ALWAYS );
        objectMapper.registerModule( new JSR310Module() );
        objectWriter = objectMapper.writerWithDefaultPrettyPrinter();
    }

    public JsonTestHelper( final Object testInstance )
    {
        this( testInstance, true );
    }

    public JsonTestHelper( final Object testInstance, final boolean prettyPrint )
    {
        this.resourceTestHelper = new ResourceTestHelper( testInstance );
        objectMapper = new ObjectMapper();
        objectMapper.setDateFormat( new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" ) );
        objectMapper.disable( SerializationFeature.WRITE_DATES_AS_TIMESTAMPS );
        objectMapper.disable( SerializationFeature.FAIL_ON_EMPTY_BEANS );
        objectMapper.enable( MapperFeature.SORT_PROPERTIES_ALPHABETICALLY );
        objectMapper.enable( SerializationFeature.WRITE_NULL_MAP_VALUES );
        objectMapper.setSerializationInclusion( JsonInclude.Include.ALWAYS );
        objectMapper.registerModule( new JSR310Module() );
        this.prettyPrint = prettyPrint;
        if ( prettyPrint )
        {
            objectWriter = objectMapper.writerWithDefaultPrettyPrinter();
        }
        else
        {
            objectWriter = objectMapper.writer();
        }
    }


    public ObjectMapper objectMapper()
    {
        return objectMapper;
    }

    public String loadTestFile( String fileName )
    {
        return resourceTestHelper.loadTestFile( fileName );
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

    public static void assertJsonEquals( final JsonNode expectedJson, final JsonNode actualJson )
    {
        assertEquals( expectedJson, actualJson );
    }

    public void assertJsonEquals2( final JsonNode expectedJson, final JsonNode actualJson )
    {

        assertEquals( jsonToString( expectedJson ), jsonToString( actualJson ) );
    }
}
