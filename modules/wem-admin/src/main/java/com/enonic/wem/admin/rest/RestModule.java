package com.enonic.wem.admin.rest;

import com.google.inject.AbstractModule;

import com.enonic.wem.admin.jsonrpc.JsonRpcHandlerBinder;
import com.enonic.wem.admin.rpc.system.GetSystemInfoRpcHandler;

public final class RestModule
    extends AbstractModule
{
    @Override
    protected void configure()
    {
        final JsonRpcHandlerBinder handlers = JsonRpcHandlerBinder.from( binder() );

        handlers.add( GetSystemInfoRpcHandler.class );
    }
}
