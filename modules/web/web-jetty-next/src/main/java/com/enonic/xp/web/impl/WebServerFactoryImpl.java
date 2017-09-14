package com.enonic.xp.web.impl;

import com.enonic.xp.web.server.WebServer;
import com.enonic.xp.web.server.WebServerConfig;
import com.enonic.xp.web.server.WebServerFactory;

public final class WebServerFactoryImpl
    implements WebServerFactory
{
    @Override
    public WebServer create( final WebServerConfig config )
    {
        return new WebServerImpl( config );
    }
}
