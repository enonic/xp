package com.enonic.wem.web;

import com.google.inject.AbstractModule;

import com.enonic.wem.core.CoreModule;

public final class WebModule
    extends AbstractModule
{
    @Override
    protected void configure()
    {
        install( new CoreModule() );
        // install( new JsonRpcModule() );
        // install( new RestModule() );

        // bind( JspDataTools.class ).asEagerSingleton();
    }
}
