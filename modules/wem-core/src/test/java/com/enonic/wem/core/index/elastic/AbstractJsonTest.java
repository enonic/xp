package com.enonic.wem.core.index.elastic;

import java.net.URL;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.JsonNodeFactory;

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
        final JsonFactory factory = mapper.getJsonFactory();
        final URL resource = getClass().getResource( fileName );

        if ( resource == null )
        {
            throw new RuntimeException( "Could not find resource with name: " + fileName );
        }

        final JsonParser parser = factory.createJsonParser( resource );
        return parser.readValueAsTree();
    }

    public JsonNode parseJsonString( final String jsonString )
        throws Exception
    {
        final ObjectMapper mapper = ObjectMapperTestHelper.create();
        final JsonFactory factory = mapper.getJsonFactory();
        final JsonParser parser = factory.createJsonParser( jsonString );
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
