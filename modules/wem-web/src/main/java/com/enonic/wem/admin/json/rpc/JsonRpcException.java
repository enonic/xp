package com.enonic.wem.admin.json.rpc;

public final class JsonRpcException
    extends Exception
{
    private final JsonRpcError error;

    public JsonRpcException( final JsonRpcError error )
    {
        super( error.getMessage() );
        this.error = error;
    }

    public JsonRpcError getError()
    {
        return this.error;
    }
}
