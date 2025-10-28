package com.enonic.xp.web.jetty.impl.configurator;

import org.eclipse.jetty.server.ForwardedRequestCustomizer;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class HttpConfiguratorTest
    extends ConnectorConfiguratorTest
{
    @Override
    protected JettyConfigurator<Server> newConfigurator()
    {
        return new HttpConfigurator();
    }

    @Test
    void defaultConfig()
    {
        configure();

        final ServerConnector connector = getConnector();
        assertNotNull( connector );
        assertEquals( null, connector.getHost() );
        assertEquals( 8080, connector.getPort() );
        assertEquals( 60000, connector.getIdleTimeout() );

        final HttpConnectionFactory factory = connector.getConnectionFactory( HttpConnectionFactory.class );
        assertNotNull( factory );

        final HttpConfiguration configuration = factory.getHttpConfiguration();
        assertNotNull( configuration );
        assertEquals( true, configuration.getSendDateHeader() );
        assertEquals( false, configuration.getSendXPoweredBy() );
        assertEquals( false, configuration.getSendServerVersion() );
        assertNotNull( configuration.getCustomizer( ForwardedRequestCustomizer.class ) );
        assertEquals( 32 * 1024, configuration.getRequestHeaderSize() );
        assertEquals( 32 * 1024, configuration.getResponseHeaderSize() );
    }

    @Test
    void notEnabled()
    {
        Mockito.when( this.config.http_enabled() ).thenReturn( false );

        configure();

        final ServerConnector connector = getConnector();
        assertNull( connector );
    }

    @Test
    void overrideConfig()
    {
        Mockito.when( this.config.host() ).thenReturn( "localhost" );
        Mockito.when( this.config.timeout() ).thenReturn( 10 );
        Mockito.when( this.config.http_xp_port() ).thenReturn( 9999 );
        Mockito.when( this.config.sendServerHeader() ).thenReturn( true );
        Mockito.when( this.config.http_requestHeaderSize() ).thenReturn( 8000 );
        Mockito.when( this.config.http_responseHeaderSize() ).thenReturn( 9000 );

        configure();

        final ServerConnector connector = getConnector();
        assertNotNull( connector );
        assertEquals( "localhost", connector.getHost() );
        assertEquals( 9999, connector.getPort() );
        assertEquals( 10, connector.getIdleTimeout() );

        final HttpConnectionFactory factory = connector.getConnectionFactory( HttpConnectionFactory.class );
        assertNotNull( factory );

        final HttpConfiguration configuration = factory.getHttpConfiguration();
        assertNotNull( configuration );
        assertEquals( true, configuration.getSendDateHeader() );
        assertEquals( true, configuration.getSendXPoweredBy() );
        assertEquals( true, configuration.getSendServerVersion() );
        assertNotNull( configuration.getCustomizer( ForwardedRequestCustomizer.class ) );
        assertEquals( 8000, configuration.getRequestHeaderSize() );
        assertEquals( 9000, configuration.getResponseHeaderSize() );
    }
}
