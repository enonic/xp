package com.enonic.wem.web.data;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;
import org.junit.Before;

import com.enonic.wem.web.jsonrpc.JsonRpcHandler;
import com.enonic.wem.web.jsonrpc.processor.JsonRpcProcessorImpl;
import com.enonic.wem.web.jsonrpc.processor.JsonRpcRequest;
import com.enonic.wem.web.jsonrpc.processor.JsonRpcResponse;
import com.enonic.wem.web.rest2.provider.ObjectMapperFactory;

import static org.junit.Assert.*;

public abstract class AbstractRpcHandlerTest
{
    private JsonRpcProcessorImpl processor;

    private JsonRpcHandler handler;

    @Before
    public void setupProcessor()
        throws Exception
    {
        this.handler = createHandler();
        this.processor = new JsonRpcProcessorImpl();
        this.processor.setHandlers( this.handler );
    }

    protected abstract JsonRpcHandler createHandler()
        throws Exception;

    protected final void testSuccess( final String paramsFile, final String resultFile )
        throws Exception
    {
        final JsonNode paramsJson = parseJson( paramsFile );
        assertNotNull( paramsJson );
        assertTrue( paramsJson instanceof ObjectNode );

        final JsonRpcRequest req = new JsonRpcRequest();
        req.setId( "1" );
        req.setMethod( this.handler.getName() );
        req.setParams( (ObjectNode) paramsJson );

        final JsonRpcResponse res = this.processor.process( req );
        assertNotNull( res );
        assertFalse( res.hasError() );

        final JsonNode result = res.getResult();
        assertNotNull( result );
        assertJson( resultFile, result );
    }

    private JsonNode parseJson( final String fileName )
        throws Exception
    {
        if ( fileName == null )
        {
            return JsonNodeFactory.instance.objectNode();
        }

        final ObjectMapper mapper = ObjectMapperFactory.create();
        final JsonFactory factory = mapper.getJsonFactory();
        final JsonParser parser = factory.createJsonParser( getClass().getResource( fileName ) );
        return parser.readValueAsTree();
    }

    private String toJson( final Object value )
        throws Exception
    {
        final ObjectMapper mapper = ObjectMapperFactory.create();
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString( value );
    }

    private void assertJson( final String expectedFile, final JsonNode actual )
        throws Exception
    {
        assertJson( parseJson( expectedFile ), actual );
    }

    private void assertJson( final JsonNode expected, final JsonNode actual )
        throws Exception
    {
        final String expectedStr = toJson( expected );
        final String actualStr = toJson( actual );

        assertEquals( expectedStr, actualStr );
    }
}
