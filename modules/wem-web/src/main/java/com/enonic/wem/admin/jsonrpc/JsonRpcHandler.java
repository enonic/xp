package com.enonic.wem.admin.jsonrpc;

public abstract class JsonRpcHandler
{
    private final String name;

    public JsonRpcHandler( final String name )
    {
        this.name = name;
    }

    public final String getName()
    {
        return this.name;
    }

    public abstract void handle( final JsonRpcContext context )
        throws Exception;
}
