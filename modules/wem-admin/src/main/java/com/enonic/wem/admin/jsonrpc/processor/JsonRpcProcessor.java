package com.enonic.wem.admin.jsonrpc.processor;

public interface JsonRpcProcessor
{
    public JsonRpcResponse process( final JsonRpcRequest req );
}
