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
        Mockito.when( this.config.http_port() ).thenReturn( 9999 );
        Mockito.when( this.config.sendServerHeader() ).thenReturn( true );

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
    }
}