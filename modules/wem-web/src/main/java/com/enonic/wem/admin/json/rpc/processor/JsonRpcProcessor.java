package com.enonic.wem.admin.json.rpc.processor;

public interface JsonRpcProcessor
{
    public JsonRpcResponse process( final JsonRpcRequest req );
}
