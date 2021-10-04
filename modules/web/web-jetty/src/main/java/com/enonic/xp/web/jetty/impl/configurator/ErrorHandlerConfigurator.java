package com.enonic.xp.web.jetty.impl.configurator;

import org.eclipse.jetty.server.Server;

import com.enonic.xp.server.RunMode;
import com.enonic.xp.web.jetty.impl.LastResortErrorHandler;

public class ErrorHandlerConfigurator
{
    public void configure( RunMode runMode, Server server )
    {
        if ( runMode != RunMode.DEV )
        {
            server.setErrorHandler( new LastResortErrorHandler() );
        }
    }

}
