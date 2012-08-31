package com.enonic.wem.web.rpc.processor;

import com.google.common.collect.ImmutableMap;

import com.enonic.wem.web.rpc.WebRpcError;
import com.enonic.wem.web.rpc.WebRpcException;
import com.enonic.wem.web.rpc.WebRpcHandler;

final class WebRpcHandlerMap
{
    private final ImmutableMap<String, WebRpcHandler> map;

    public WebRpcHandlerMap( final WebRpcHandler... handlers )
    {
        final ImmutableMap.Builder<String, WebRpcHandler> builder = ImmutableMap.builder();
        for ( final WebRpcHandler handler : handlers )
        {
            builder.put( handler.getName(), handler );
        }

        this.map = builder.build();
    }

    public WebRpcHandler getHandler( final String name )
        throws WebRpcException
    {
        final WebRpcHandler handler = this.map.get( name );
        if ( handler != null )
        {
            return handler;
        }

        final WebRpcError error = WebRpcError.methodNotFound( name );
        throw new WebRpcException( error );
    }
}
