package com.enonic.xp.web.jetty.impl.configurator;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.junit.jupiter.api.Test;

import com.enonic.xp.server.RunMode;
import com.enonic.xp.web.jetty.impl.LastResortErrorHandler;

import static org.assertj.core.api.Assertions.assertThat;

class ErrorHandlerConfiguratorTest
{
    @Test
    void testConfigure_prod()
    {
        final Server server = new Server();
        new ErrorHandlerConfigurator().configure( RunMode.PROD, server );
        final Request.Handler errorHandler = server.getErrorHandler();
        assertThat( errorHandler ).isInstanceOf( LastResortErrorHandler.class );

    }
}
