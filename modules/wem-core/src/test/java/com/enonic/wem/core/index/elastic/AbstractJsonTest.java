package com.enonic.wem.core.index.elastic;

import java.net.URL;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

import static org.junit.Assert.*;

public class AbstractJsonTest
{

    public JsonNode parseJson( final String fileName )
        throws Exception
    {
        if ( fileName == null )
        {
            return JsonNodeFactory.instance.objectNode();
        }

        final ObjectMapper mapper = ObjectMapperTestHelper.create();
        final JsonFactory factory = mapper.getFactory();
        final URL resource = getClass().getResource( fileName );

        if ( resource == null )
        {
            throw new RuntimeException( "Could not find resource with name: " + fileName );
        }

        final JsonParser parser = factory.createParser( resource );
        return parser.readValueAsTree();
    }

    public JsonNode parseJsonString( final String jsonString )
        throws Exception
    {
        final ObjectMapper mapper = ObjectMapperTestHelper.create();
        final JsonFactory factory = mapper.getFactory();
        final JsonParser parser = factory.createParser( jsonString );
        return parser.readValueAsTree();
    }


    public String toJson( final Object value )
        throws Exception
    {
        final ObjectMapper mapper = ObjectMapperTestHelper.create();
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString( value );
    }

    public void assertJson( final JsonNode expected, final JsonNode actual )
        throws Exception
    {
        final String expectedStr = toJson( expected );
        final String actualStr = toJson( actual );

        assertEquals( expectedStr, actualStr );
    }
}
