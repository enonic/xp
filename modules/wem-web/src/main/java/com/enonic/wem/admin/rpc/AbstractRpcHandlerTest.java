package com.enonic.wem.admin.rpc;

import javax.servlet.http.HttpServletRequest;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;
import org.junit.Before;
import org.mockito.Mockito;

import com.google.common.collect.Sets;

import com.enonic.wem.admin.json.rpc.JsonRpcError;
import com.enonic.wem.admin.json.rpc.JsonRpcHandler;
import com.enonic.wem.admin.json.rpc.processor.JsonRpcProcessorImpl;
import com.enonic.wem.admin.json.rpc.processor.JsonRpcRequest;
import com.enonic.wem.admin.json.rpc.processor.JsonRpcResponse;
import com.enonic.wem.web.servlet.ServletRequestHolder;

import static org.junit.Assert.*;

public abstract class AbstractRpcHandlerTest
    extends AbstractJsonTest
{
    private JsonRpcProcessorImpl processor;

    private JsonRpcHandler handler;

    @Before
    public void setupProcessor()
        throws Exception
    {
        this.handler = createHandler();
        this.processor = new JsonRpcProcessorImpl();
        this.processor.setHandlers( Sets.newHashSet( this.handler ) );

        mockCurrentContextHttpRequest();
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
        processAndTestJson( null, resultJson );
    }

    protected final void testSuccess( final JsonNode resultJson )
        throws Exception
    {
        processAndTestJson( null, resultJson );
    }

    protected final void testSuccess( final String paramsFile, final String resultFile )
        throws Exception
    {
        final JsonNode paramsJson = parseJson( paramsFile );
        final JsonNode resultJson = parseJson( resultFile );
        assertNotNull( paramsJson );
        assertTrue( paramsJson instanceof ObjectNode );
        processAndTestJson( paramsJson, resultJson );
    }

    protected final void testSuccess( final String paramsFile, final JsonNode resultJson )
        throws Exception
    {
        final JsonNode paramsJson = parseJson( paramsFile );
        assertNotNull( paramsJson );
        assertTrue( paramsJson instanceof ObjectNode );
        processAndTestJson( paramsJson, resultJson );
    }

    protected final void testSuccess( final JsonNode paramsJson, final String resultFile )
        throws Exception
    {
        final JsonNode resultJson = parseJson( resultFile );
        assertNotNull( paramsJson );
        assertTrue( paramsJson instanceof ObjectNode );
        processAndTestJson( paramsJson, resultJson );
    }

    protected final void testSuccess( final JsonNode paramsJson, final JsonNode resultJson )
        throws Exception
    {
        assertNotNull( paramsJson );
        assertTrue( paramsJson instanceof ObjectNode );
        processAndTestJson( paramsJson, resultJson );
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

    private void processAndTestJson( final JsonNode paramsJson, final JsonNode resultJson )
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

    private void mockCurrentContextHttpRequest()
    {
        final HttpServletRequest req = Mockito.mock( HttpServletRequest.class );
        Mockito.when( req.getScheme() ).thenReturn( "http" );
        Mockito.when( req.getServerName() ).thenReturn( "localhost" );
        Mockito.when( req.getLocalPort() ).thenReturn( 80 );
        ServletRequestHolder.setRequest( req );
    }
}
