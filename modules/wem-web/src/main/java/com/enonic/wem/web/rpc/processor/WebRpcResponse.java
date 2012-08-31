package com.enonic.wem.web.rpc.processor;

import org.codehaus.jackson.JsonNode;

public final class WebRpcResponse
    extends WebRpcMessage
{
    private JsonNode result;

    public JsonNode getResult()
    {
        return this.result;
    }

    public void setResult( final JsonNode result )
    {
        this.result = result;
    }

    public static WebRpcResponse from( final WebRpcRequest req )
    {
        final WebRpcResponse res = new WebRpcResponse();
        res.setId( req.getId() );
        res.setMethod( req.getMethod() );
        res.setAction( req.getAction() );
        res.setError( req.getError() );
        return res;
    }
}
