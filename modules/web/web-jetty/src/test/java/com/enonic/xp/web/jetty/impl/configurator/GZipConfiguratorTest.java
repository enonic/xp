package com.enonic.xp.web.jetty.impl.configurator;

import org.eclipse.jetty.server.handler.gzip.GzipHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class GZipConfiguratorTest
    extends JettyConfiguratorTest<ServletContextHandler>
{
    private ServletContextHandler context;

    @Override
    protected ServletContextHandler setupObject()
    {
        this.context = new ServletContextHandler();
        return this.context;
    }

    @Override
    protected JettyConfigurator<ServletContextHandler> newConfigurator()
    {
        return new GZipConfigurator();
    }

    private GzipHandler getHandler()
    {
        return this.context.getGzipHandler();
    }

    @Test
    public void testConfigure()
    {
        configure();

        final GzipHandler handler = getHandler();
        assertNotNull( handler );
        assertEquals( 23, handler.getMinGzipSize() );
    }

    @Test
    public void testConfigure_disabled()
    {
        Mockito.when( this.config.gzip_enabled() ).thenReturn( false );

        configure();

        assertNull( getHandler() );
    }

    @Test
    public void testConfigure_override()
    {
        Mockito.when( this.config.gzip_enabled() ).thenReturn( true );
        Mockito.when( this.config.gzip_minSize() ).thenReturn( 100 );

        configure();

        final GzipHandler handler = getHandler();
        assertNotNull( handler );
        assertEquals( 100, handler.getMinGzipSize() );
    }
}
