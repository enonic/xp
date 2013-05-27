package com.enonic.wem.admin;

import com.google.inject.AbstractModule;

import com.enonic.wem.admin.json.rpc.JsonRpcModule;
import com.enonic.wem.admin.rest.RestModule;

public final class AdminModule
    extends AbstractModule
{
    @Override
    protected void configure()
    {
        install( new JsonRpcModule() );
        install( new RestModule() );
    }
}
