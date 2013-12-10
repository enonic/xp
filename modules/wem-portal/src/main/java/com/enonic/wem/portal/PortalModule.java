package com.enonic.wem.portal;

import com.google.inject.AbstractModule;

import com.enonic.wem.portal.script.ScriptModule;
import com.enonic.wem.web.WebInitializerBinder;

public final class PortalModule
    extends AbstractModule
{
    @Override
    protected void configure()
    {
        install( new ScriptModule() );
        WebInitializerBinder.from( binder() ).add( PortalWebInitializer.class );
    }
}
