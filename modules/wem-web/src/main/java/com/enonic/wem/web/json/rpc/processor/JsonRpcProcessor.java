package com.enonic.wem.web.json.rpc.processor;

public interface JsonRpcProcessor
{
    public JsonRpcResponse process( final JsonRpcRequest req );
}
