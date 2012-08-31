package com.enonic.wem.web.rpc.processor;

import com.enonic.wem.web.rpc.WebRpcError;

public abstract class WebRpcMessage
{
    private String id;

    private String method;

    private String action;

    private WebRpcError error;

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

    public final String getAction()
    {
        return this.action;
    }

    public final void setAction( final String action )
    {
        this.action = action;
    }

    public final WebRpcError getError()
    {
        return this.error;
    }

    public final void setError( final WebRpcError error )
    {
        this.error = error;
    }

    public final boolean hasError()
    {
        return this.error != null;
    }
}
