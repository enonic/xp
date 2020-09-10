package com.enonic.xp.web.jetty.impl.configurator;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ErrorHandler;
import org.junit.jupiter.api.Test;

import com.enonic.xp.server.RunMode;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ErrorHandlerConfiguratorTest
{
    @Test
    void testConfigure_prod()
    {
        final Server server = new Server();
        new ErrorHandlerConfigurator().configure( RunMode.PROD, server );
        final ErrorHandler errorHandler = server.getErrorHandler();
        assertAll( () -> assertFalse( errorHandler.isShowStacks() ), () -> assertFalse( errorHandler.isShowServlet() ) );
    }

    @Test
    void testConfigure_dev()
    {
        final Server server = new Server();
        new ErrorHandlerConfigurator().configure( RunMode.DEV, server );
        final ErrorHandler errorHandler = server.getErrorHandler();
        assertAll( () -> assertTrue( errorHandler.isShowStacks() ), () -> assertTrue( errorHandler.isShowServlet() ) );
    }
}
