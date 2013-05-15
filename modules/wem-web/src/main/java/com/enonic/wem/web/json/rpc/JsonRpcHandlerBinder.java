package com.enonic.wem.web.json.rpc;

import com.google.inject.Binder;
import com.google.inject.Scopes;
import com.google.inject.multibindings.Multibinder;

public final class JsonRpcHandlerBinder
{
    private final Multibinder<JsonRpcHandler> binder;

    private JsonRpcHandlerBinder( final Binder binder )
    {
        this.binder = Multibinder.newSetBinder( binder, JsonRpcHandler.class );
    }

    public void add( final Class<? extends JsonRpcHandler> handler )
    {
        this.binder.addBinding().to( handler ).in( Scopes.SINGLETON );
    }

    public static JsonRpcHandlerBinder from( final Binder binder )
    {
        return new JsonRpcHandlerBinder( binder );
    }
}
