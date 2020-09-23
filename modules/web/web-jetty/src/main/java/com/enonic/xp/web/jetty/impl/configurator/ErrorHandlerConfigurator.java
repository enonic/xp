package com.enonic.xp.web.jetty.impl.configurator;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ErrorHandler;

import com.enonic.xp.server.RunMode;

public class ErrorHandlerConfigurator
{
    public void configure( RunMode runMode, Server server )
    {
        final ErrorHandler errorHandler = new ErrorHandler();
        if ( runMode != RunMode.DEV )
        {
            errorHandler.setShowStacks( false );
            errorHandler.setShowServlet( false );
        }
        server.setErrorHandler( errorHandler );
    }
}
