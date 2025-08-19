package com.enonic.xp.web.jetty.impl.configurator;

import org.eclipse.jetty.compression.gzip.GzipCompression;
import org.eclipse.jetty.compression.server.CompressionHandler;
import org.eclipse.jetty.ee11.servlet.ServletContextHandler;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class GZipConfiguratorTest
    extends JettyConfiguratorTest<ServletContextHandler>
{
    @Override
    protected ServletContextHandler setupObject()
    {
        return mock( ServletContextHandler.class );
    }

    @Override
    protected JettyConfigurator<ServletContextHandler> newConfigurator()
    {
        return new GZipConfigurator();
    }

    @Test
    void testConfigure()
    {
        when( this.config.gzip_enabled() ).thenReturn( true );

        configure();

        ArgumentCaptor<CompressionHandler> captor = ArgumentCaptor.forClass( CompressionHandler.class );
        verify( object ).insertHandler( captor.capture() );
        final CompressionHandler handler = captor.getValue();

        assertNotNull( handler );
        assertEquals( 32, handler.getBean( GzipCompression.class ).getMinCompressSize() );
    }

    @Test
    void testConfigure_disabled()
    {
        when( this.config.gzip_enabled() ).thenReturn( false );

        configure();

        verifyNoInteractions( object );
    }

    @Test
    void testConfigure_override()
    {
        when( this.config.gzip_enabled() ).thenReturn( true );
        when( this.config.gzip_minSize() ).thenReturn( 100 );

        configure();

        ArgumentCaptor<CompressionHandler> captor = ArgumentCaptor.forClass( CompressionHandler.class );
        verify( object ).insertHandler( captor.capture() );
        final CompressionHandler handler = captor.getValue();

        assertEquals( 100, handler.getBean( GzipCompression.class ).getMinCompressSize() );
    }
}
