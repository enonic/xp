package com.enonic.wem.web.rpc.processor;

public interface JsonRpcProcessor
{
    public JsonRpcResponse process( final JsonRpcRequest req );
}
