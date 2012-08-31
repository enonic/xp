package com.enonic.wem.web.rpc;

public abstract class WebRpcHandler
{
    private final String name;

    public WebRpcHandler( final String name )
    {
        this.name = name;
    }

    public final String getName()
    {
        return this.name;
    }

    public abstract void handle( final WebRpcContext context )
        throws Exception;
}
