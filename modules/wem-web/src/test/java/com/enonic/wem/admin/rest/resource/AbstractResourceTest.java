package com.enonic.wem.admin.rest.resource;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;

import com.enonic.wem.admin.json.JsonResult;
import com.enonic.wem.admin.json.ObjectMapperHelper;

import static org.junit.Assert.*;

public abstract class AbstractResourceTest
{
    private final ObjectMapper mapper;

    public AbstractResourceTest()
    {
        this.mapper = ObjectMapperHelper.create();
    }

    protected final void assertJsonResult( final String name, final JsonResult result )
        throws Exception
    {
        final JsonNode node = result.toJson();

        assertNotNull( node );
        assertJson( name, node );
    }

    protected final void assertJson( final String name, final JsonNode node )
        throws Exception
    {
        final JsonNode expectedNode = parseJson( name );

        final String expectedStr = toJson( expectedNode );
        final String actualStr = toJson( node );

        assertEquals( expectedStr, actualStr );
    }

    private String toJson( final Object value )
        throws Exception
    {
        final ObjectMapper mapper = ObjectMapperHelper.create();
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString( value );
    }

    private JsonNode parseJson( final String name )
        throws Exception
    {
        final ObjectMapper mapper = ObjectMapperHelper.create();
        final JsonFactory factory = mapper.getJsonFactory();
        final JsonParser parser = factory.createJsonParser( getClass().getResource( name ) );
        return parser.readValueAsTree();
    }
}
