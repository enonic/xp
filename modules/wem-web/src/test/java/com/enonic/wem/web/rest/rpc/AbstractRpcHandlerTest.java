package com.enonic.wem.web.rest.rpc;

import javax.servlet.http.HttpServletRequest;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;
import org.junit.Before;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.enonic.wem.web.json.ObjectMapperHelper;
import com.enonic.wem.web.json.rpc.JsonRpcError;
import com.enonic.wem.web.json.rpc.JsonRpcHandler;
import com.enonic.wem.web.json.rpc.processor.JsonRpcProcessorImpl;
import com.enonic.wem.web.json.rpc.processor.JsonRpcRequest;
import com.enonic.wem.web.json.rpc.processor.JsonRpcResponse;

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


    protected ObjectNode objectNode()
    {
        return JsonNodeFactory.instance.objectNode();
    }

    protected ArrayNode arrayNode()
    {
        return JsonNodeFactory.instance.arrayNode();
    }


    protected final void testSuccess( final String resultFile )
        throws Exception
    {
        final JsonNode resultJson = parseJson( resultFile );
        testJson( null, resultJson );
    }

    protected final void testSuccess( final JsonNode resultJson )
        throws Exception
    {
        testJson( null, resultJson );
    }

    protected final void testSuccess( final String paramsFile, final String resultFile )
        throws Exception
    {
        final JsonNode paramsJson = parseJson( paramsFile );
        final JsonNode resultJson = parseJson( resultFile );
        assertNotNull( paramsJson );
        assertTrue( paramsJson instanceof ObjectNode );
        testJson( paramsJson, resultJson );
    }

    protected final void testSuccess( final String paramsFile, final JsonNode resultJson )
        throws Exception
    {
        final JsonNode paramsJson = parseJson( paramsFile );
        assertNotNull( paramsJson );
        assertTrue( paramsJson instanceof ObjectNode );
        testJson( paramsJson, resultJson );
    }

    protected final void testSuccess( final JsonNode paramsJson, final String resultFile )
        throws Exception
    {
        final JsonNode resultJson = parseJson( resultFile );
        assertNotNull( paramsJson );
        assertTrue( paramsJson instanceof ObjectNode );
        testJson( paramsJson, resultJson );
    }

    protected final void testSuccess( final JsonNode paramsJson, final JsonNode resultJson )
        throws Exception
    {
        assertNotNull( paramsJson );
        assertTrue( paramsJson instanceof ObjectNode );
        testJson( paramsJson, resultJson );
    }

    protected final void testError( final JsonNode paramsJson, String message )
    {
        final JsonRpcRequest req = new JsonRpcRequest();
        req.setId( "1" );
        req.setMethod( this.handler.getName() );
        if ( paramsJson != null )
        {
            req.setParams( (ObjectNode) paramsJson );
        }

        final JsonRpcResponse res = this.processor.process( req );
        assertNotNull( res );
        assertTrue( res.hasError() );

        final JsonRpcError error = res.getError();
        assertNotNull( error );
        assertEquals( error.getMessage(), message );
    }


    protected JsonNode getJsonResult( final JsonNode paramsJson )
    {
        final JsonRpcRequest req = new JsonRpcRequest();
        req.setId( "1" );
        req.setMethod( this.handler.getName() );
        if ( paramsJson != null )
        {
            req.setParams( (ObjectNode) paramsJson );
        }

        final JsonRpcResponse res = this.processor.process( req );
        assertNotNull( res );
        assertFalse( res.hasError() );

        final JsonNode result = res.getResult();
        assertNotNull( result );
        return result;
    }

    private void testJson( final JsonNode paramsJson, final JsonNode resultJson )
        throws Exception
    {
        final JsonRpcRequest req = new JsonRpcRequest();
        req.setId( "1" );
        req.setMethod( this.handler.getName() );
        if ( paramsJson != null )
        {
            req.setParams( (ObjectNode) paramsJson );
        }

        final JsonRpcResponse res = this.processor.process( req );
        assertNotNull( res );
        if ( res.hasError() )
        {
            fail( "Json has error: " + res.getError().getMessage() );
        }

        final JsonNode result = res.getResult();
        assertNotNull( result );
        assertJson( resultJson, result );
    }

    protected JsonNode parseJson( final String fileName )
        throws Exception
    {
        if ( fileName == null )
        {
            return JsonNodeFactory.instance.objectNode();
        }

        final ObjectMapper mapper = ObjectMapperHelper.create();
        final JsonFactory factory = mapper.getJsonFactory();
        final JsonParser parser = factory.createJsonParser( getClass().getResource( fileName ) );
        return parser.readValueAsTree();
    }

    private String toJson( final Object value )
        throws Exception
    {
        final ObjectMapper mapper = ObjectMapperHelper.create();
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString( value );
    }

    private void assertJson( final JsonNode expected, final JsonNode actual )
        throws Exception
    {
        final String expectedStr = toJson( expected );
        final String actualStr = toJson( actual );

        assertEquals( expectedStr, actualStr );
    }

    protected void mockCurrentContextHttpRequest()
    {
        final HttpServletRequest req = new MockHttpServletRequest();
        final ServletRequestAttributes attrs = new ServletRequestAttributes( req );
        RequestContextHolder.setRequestAttributes( attrs );
    }
}
