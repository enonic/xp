package com.enonic.wem.portal;

import javax.inject.Singleton;

import com.google.inject.AbstractModule;

import com.enonic.wem.portal.script.RhinoScriptLoader;
import com.enonic.wem.portal.script.RhinoScriptLoaderImpl;
import com.enonic.wem.web.WebInitializerBinder;

public final class PortalModule
    extends AbstractModule
{
    @Override
    protected void configure()
    {
        WebInitializerBinder.from( binder() ).add( PortalWebInitializer.class );
        bind( RhinoScriptLoader.class ).to( RhinoScriptLoaderImpl.class ).in( Singleton.class );
    }
}
