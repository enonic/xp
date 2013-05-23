package com.enonic.wem.admin.json.rpc.processor;

import com.enonic.wem.admin.json.rpc.JsonRpcError;

public abstract class JsonRpcMessage
{
    private String id;

    private String method;

    private JsonRpcError error;

    public final String getId()
    {
        return this.id;
    }

    public final void setId( final String id )
    {
        this.id = id;
    }

    public final String getMethod()
    {
        return this.method;
    }

    public final void setMethod( final String method )
    {
        this.method = method;
    }

    public final JsonRpcError getError()
    {
        return this.error;
    }

    public final void setError( final JsonRpcError error )
    {
        this.error = error;
    }

    public final boolean hasError()
    {
        return this.error != null;
    }
}
