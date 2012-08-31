package com.enonic.wem.web.rpc.processor;

public interface WebRpcProcessor
{
    public WebRpcResponse process( final WebRpcRequest req );
}
