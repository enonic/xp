package com.enonic.wem.admin.json.rpc;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

import com.enonic.wem.admin.json.rpc.controller.JsonRpcController;
import com.enonic.wem.admin.json.rpc.processor.JsonRpcProcessor;
import com.enonic.wem.admin.json.rpc.processor.JsonRpcProcessorImpl;

public final class JsonRpcModule
    extends AbstractModule
{
    @Override
    protected void configure()
    {
        bind( JsonRpcProcessor.class ).to( JsonRpcProcessorImpl.class ).in( Scopes.SINGLETON );
        bind( JsonRpcController.class ).in( Scopes.SINGLETON );
    }
}
