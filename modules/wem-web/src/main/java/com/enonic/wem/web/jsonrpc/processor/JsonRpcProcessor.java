package com.enonic.wem.web.jsonrpc.processor;

public interface JsonRpcProcessor
{
    public JsonRpcResponse process( final JsonRpcRequest req );
}
