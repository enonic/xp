package com.enonic.xp.web.jetty.impl.configurator;

import org.eclipse.jetty.server.Server;

import com.enonic.xp.server.RunMode;
import com.enonic.xp.web.jetty.impl.LastResortErrorHandler;

public class ErrorHandlerConfigurator
{
    public void configure( Server server )
    {
        if ( RunMode.isProd() )
        {
            server.setErrorHandler( new LastResortErrorHandler() );
        }
    }
}
