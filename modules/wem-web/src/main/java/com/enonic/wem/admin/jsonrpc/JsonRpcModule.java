package com.enonic.wem.admin.jsonrpc;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

import com.enonic.wem.admin.jsonrpc.processor.JsonRpcProcessor;
import com.enonic.wem.admin.jsonrpc.processor.JsonRpcProcessorImpl;

public final class JsonRpcModule
    extends AbstractModule
{
    @Override
    protected void configure()
    {
        bind( JsonRpcProcessor.class ).to( JsonRpcProcessorImpl.class ).in( Scopes.SINGLETON );
    }
}
