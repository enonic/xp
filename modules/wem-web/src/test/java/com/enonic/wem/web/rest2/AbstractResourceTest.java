package com.enonic.wem.web.rest2;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;

import com.enonic.wem.web.rest2.common.JsonResult;
import com.enonic.wem.web.rest2.provider.ObjectMapperFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public abstract class AbstractResourceTest
{
    private final ObjectMapper mapper;

    public AbstractResourceTest()
    {
        this.mapper = ObjectMapperFactory.create();
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
        final ObjectMapper mapper = ObjectMapperFactory.create();
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString( value );
    }

    private JsonNode parseJson( final String name )
        throws Exception
    {
        final ObjectMapper mapper = ObjectMapperFactory.create();
        final JsonFactory factory = mapper.getJsonFactory();
        final JsonParser parser = factory.createJsonParser( getClass().getResource( name ) );
        return parser.readValueAsTree();
    }
}
