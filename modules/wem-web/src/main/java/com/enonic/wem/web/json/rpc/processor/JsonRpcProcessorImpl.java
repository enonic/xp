package com.enonic.wem.web.json.rpc.processor;

import org.codehaus.jackson.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.inject.Inject;
import org.springframework.stereotype.Component;

import com.enonic.wem.web.json.rpc.JsonRpcError;
import com.enonic.wem.web.json.rpc.JsonRpcException;
import com.enonic.wem.web.json.rpc.JsonRpcHandler;

@Component
public final class JsonRpcProcessorImpl
    implements JsonRpcProcessor
{
    private final static Logger LOG = LoggerFactory.getLogger( JsonRpcProcessorImpl.class );

    private JsonRpcHandlerMap handlerMap;

    @Override
    public JsonRpcResponse process( final JsonRpcRequest req )
    {
        final JsonRpcResponse res = JsonRpcResponse.from( req );

        try
        {
            res.setResult( doProcess( req ) );
        }
        catch ( final JsonRpcException e )
        {
            res.setError( e.getError() );
        }

        return res;
    }

    private JsonNode doProcess( final JsonRpcRequest req )
        throws JsonRpcException
    {
        if ( req.getMethod() == null )
        {
            final JsonRpcError error = JsonRpcError.invalidRequest( "Method field must be set" );
            throw new JsonRpcException( error );
        }

        final JsonRpcHandler handler = this.handlerMap.getHandler( req.getMethod() );
        final JsonRpcContextImpl context = new JsonRpcContextImpl( req.getParams() );

        try
        {
            handler.handle( context );
        }
        catch ( final JsonRpcException e )
        {
            throw e;
        }
        catch ( final Exception e )
        {
            LOG.error( "Error occurred processing request", e );
            final JsonRpcError error = JsonRpcError.internalError( e.getMessage() );
            throw new JsonRpcException( error );
        }

        return context.getResult();
    }

    @Inject
    public void setHandlers( final JsonRpcHandler... handlers )
    {
        this.handlerMap = new JsonRpcHandlerMap( handlers );
    }
}
