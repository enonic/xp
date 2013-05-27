package com.enonic.wem.portal;

import com.google.inject.AbstractModule;

import com.enonic.wem.admin.json.rpc.JsonRpcModule;
import com.enonic.wem.admin.rest.RestModule;

public final class PortalModule
    extends AbstractModule
{
    @Override
    protected void configure()
    {
        install( new PortalServletModule() );
    }
}
