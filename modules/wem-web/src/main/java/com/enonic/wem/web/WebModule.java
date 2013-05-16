package com.enonic.wem.web;

import com.google.inject.AbstractModule;

import com.enonic.wem.core.CoreModule;
import com.enonic.wem.web.json.rpc.JsonRpcModule;
import com.enonic.wem.web.jsp.JspDataTools;
import com.enonic.wem.web.rest.RestModule;

public final class WebModule
    extends AbstractModule
{
    @Override
    protected void configure()
    {
        install( new CoreModule() );
        install( new JsonRpcModule() );
        install( new RestModule() );

        bind( JspDataTools.class ).asEagerSingleton();
    }
}
