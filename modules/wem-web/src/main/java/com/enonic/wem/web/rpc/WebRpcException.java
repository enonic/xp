package com.enonic.wem.web.rpc;

public final class WebRpcException
    extends Exception
{
    private final WebRpcError error;

    public WebRpcException( final WebRpcError error )
    {
        super( error.getMessage() );
        this.error = error;
    }

    public WebRpcError getError()
    {
        return this.error;
    }
}
