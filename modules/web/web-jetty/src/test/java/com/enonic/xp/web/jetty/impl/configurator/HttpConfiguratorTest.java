package com.enonic.xp.web.jetty.impl.configurator;

import org.eclipse.jetty.server.ForwardedRequestCustomizer;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.*;

public class HttpConfiguratorTest
    extends ConnectorConfiguratorTest
{
    @Override
    protected JettyConfigurator<Server> newConfigurator()
    {
        return new HttpConfigurator();
    }

    @Test
    public void defaultConfig()
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
    public void notEnabled()
    {
        Mockito.when( this.config.http_enabled() ).thenReturn( false );

        configure();

        final ServerConnector connector = getConnector();
        assertNull( connector );
    }

    @Test
    public void overrideConfig()
    {
        Mockito.when( this.config.host() ).thenReturn( "localhost" );
        Mockito.when( this.config.timeout() ).thenReturn( 10 );
        Mockito.when( this.config.http_xp_port() ).thenReturn( 9999 );
        Mockito.when( this.config.xp_port_connection_number() ).thenReturn( 123 );
        Mockito.when( this.config.management_port_connection_number() ).thenReturn( 213 );
        Mockito.when( this.config.monitor_port_connection_number() ).thenReturn( 321 );
        Mockito.when( this.config.xp_port_connection_number() ).thenReturn( 123 );
        Mockito.when( this.config.sendServerHeader() ).thenReturn( true );
        Mockito.when( this.config.http_requestHeaderSize() ).thenReturn( 8000 );
        Mockito.when( this.config.http_responseHeaderSize() ).thenReturn( 9000 );

        configure();

        final ServerConnector connector = getConnector();
        assertNotNull( connector );
        assertEquals( "localhost", connector.getHost() );
        assertEquals( 9999, connector.getPort() );
        assertEquals( 10, connector.getIdleTimeout() );
        assertEquals( 123, connector.getAcceptors() );

        assertEquals( 213, getConnector( 1 ).getAcceptors() );
        assertEquals( 321, getConnector( 2 ).getAcceptors() );

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