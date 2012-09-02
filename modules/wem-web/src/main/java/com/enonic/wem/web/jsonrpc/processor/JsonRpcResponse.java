package com.enonic.wem.web.jsonrpc.processor;

import org.codehaus.jackson.JsonNode;

public final class JsonRpcResponse
    extends JsonRpcMessage
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

    public static JsonRpcResponse from( final JsonRpcRequest req )
    {
        final JsonRpcResponse res = new JsonRpcResponse();
        res.setId( req.getId() );
        res.setMethod( req.getMethod() );
        res.setError( req.getError() );
        return res;
    }
}
