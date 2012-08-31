package com.enonic.wem.web.rpc.processor;

import java.util.Set;

public interface WebRpcProcessor
{
    public Set<String> getMethodNames();

    public WebRpcResponse process( final WebRpcRequest req );
}
