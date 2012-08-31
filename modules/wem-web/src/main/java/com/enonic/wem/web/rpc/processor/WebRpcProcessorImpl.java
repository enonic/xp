package com.enonic.wem.web.rpc.processor;

import org.codehaus.jackson.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.wem.web.rpc.WebRpcError;
import com.enonic.wem.web.rpc.WebRpcException;
import com.enonic.wem.web.rpc.WebRpcHandler;

import com.enonic.cms.api.util.LogFacade;

@Component
public final class WebRpcProcessorImpl
    implements WebRpcProcessor
{
    private final static LogFacade LOG = LogFacade.get( WebRpcProcessorImpl.class );

    private WebRpcHandlerMap handlerMap;

    @Override
    public WebRpcResponse process( final WebRpcRequest req )
    {
        final WebRpcResponse res = WebRpcResponse.from( req );

        try
        {
            res.setResult( doProcess( req ) );
        }
        catch ( final WebRpcException e )
        {
            res.setError( e.getError() );
        }

        return res;
    }

    private JsonNode doProcess( final WebRpcRequest req )
        throws WebRpcException
    {
        if ( req.getMethod() == null )
        {
            final WebRpcError error = WebRpcError.invalidRequest( "Method field must be set" );
            throw new WebRpcException( error );
        }

        final WebRpcHandler handler = this.handlerMap.getHandler( req.getMethod() );
        final WebRpcContextImpl context = new WebRpcContextImpl( req.getParams() );

        try
        {
            handler.handle( context );
        }
        catch ( final WebRpcException e )
        {
            throw e;
        }
        catch ( final Exception e )
        {
            LOG.errorCause( "Error occurred processing webRpc request", e );
            final WebRpcError error = WebRpcError.internalError( e.getMessage() );
            throw new WebRpcException( error );
        }

        return context.getResult();
    }

    @Autowired
    public void setHandlers( final WebRpcHandler... handlers )
    {
        this.handlerMap = new WebRpcHandlerMap( handlers );
    }
}
