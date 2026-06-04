package com.enonic.xp.web.jetty.impl.configurator;

import java.util.Arrays;
import java.util.List;

import org.eclipse.jetty.server.ForwardedRequestCustomizer;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.junit.jupiter.api.Test;

import com.enonic.xp.server.RunMode;
import com.enonic.xp.server.RunModeSupport;
import com.enonic.xp.web.dispatch.DispatchConstants;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

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
        RunModeSupport.set( RunMode.PROD );

        configure();

        final ServerConnector connector = getConnector( DispatchConstants.XP_CONNECTOR );
        assertNotNull( connector );
        assertNull( connector.getHost() );
        assertEquals( 8080, connector.getPort() );
        assertEquals( 60000, connector.getIdleTimeout() );

        assertNull( getConnector( DispatchConstants.API_CONNECTOR ).getHost() );
        assertEquals( 4848, getConnector( DispatchConstants.API_CONNECTOR ).getPort() );

        assertNull( getConnector( DispatchConstants.STATUS_CONNECTOR ).getHost() );
        assertEquals( 2609, getConnector( DispatchConstants.STATUS_CONNECTOR ).getPort() );

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
    void defaultConfigInDev()
    {
        RunModeSupport.set( RunMode.DEV );

        configure();

        // in dev mode every connector binds both loopback addresses, like Elasticsearch does for _local_
        assertThat( getHosts( DispatchConstants.XP_CONNECTOR ) ).containsExactlyInAnyOrder( "127.0.0.1", "0:0:0:0:0:0:0:1" );
        assertThat( getHosts( DispatchConstants.API_CONNECTOR ) ).containsExactlyInAnyOrder( "127.0.0.1", "0:0:0:0:0:0:0:1" );
        assertThat( getHosts( DispatchConstants.STATUS_CONNECTOR ) ).containsExactlyInAnyOrder( "127.0.0.1", "0:0:0:0:0:0:0:1" );
    }

    @Test
    void notEnabled()
    {
        when( this.config.http_enabled() ).thenReturn( false );

        configure();

        final ServerConnector connector = getConnector();
        assertNull( connector );
    }

    @Test
    void overrideConfig()
    {
        RunModeSupport.set( RunMode.PROD );
        when( this.config.host() ).thenReturn( "127.0.0.1" );
        when( this.config.timeout() ).thenReturn( 10 );
        when( this.config.http_web_port() ).thenReturn( 9999 );
        when( this.config.sendServerHeader() ).thenReturn( true );
        when( this.config.http_requestHeaderSize() ).thenReturn( 8000 );
        when( this.config.http_responseHeaderSize() ).thenReturn( 9000 );

        configure();

        final ServerConnector connector = getConnector( DispatchConstants.XP_CONNECTOR );
        assertNotNull( connector );
        assertEquals( "127.0.0.1", connector.getHost() );
        assertEquals( 9999, connector.getPort() );
        assertEquals( 10, connector.getIdleTimeout() );

        assertEquals( "127.0.0.1", getConnector( DispatchConstants.API_CONNECTOR ).getHost() );
        assertEquals( "127.0.0.1", getConnector( DispatchConstants.STATUS_CONNECTOR ).getHost() );

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

    @Test
    void perConnectorHost()
    {
        RunModeSupport.set( RunMode.PROD );
        when( this.config.http_web_host() ).thenReturn( "_local_" );
        when( this.config.http_management_host() ).thenReturn( "127.0.0.2" );
        when( this.config.http_statistics_host() ).thenReturn( "_local:ipv6_" );

        configure();

        assertThat( getHosts( DispatchConstants.XP_CONNECTOR ) ).containsExactlyInAnyOrder( "127.0.0.1", "0:0:0:0:0:0:0:1" );
        assertThat( getHosts( DispatchConstants.API_CONNECTOR ) ).containsExactly( "127.0.0.2" );
        assertThat( getHosts( DispatchConstants.STATUS_CONNECTOR ) ).containsExactly( "0:0:0:0:0:0:0:1" );
    }

    @Test
    void perConnectorHostWinsOverGlobalHost()
    {
        RunModeSupport.set( RunMode.PROD );
        when( this.config.host() ).thenReturn( "127.0.0.2" );
        when( this.config.http_web_host() ).thenReturn( "_local:ipv4_" );

        configure();

        assertThat( getHosts( DispatchConstants.XP_CONNECTOR ) ).containsExactly( "127.0.0.1" );
        assertThat( getHosts( DispatchConstants.API_CONNECTOR ) ).containsExactly( "127.0.0.2" );
        assertThat( getHosts( DispatchConstants.STATUS_CONNECTOR ) ).containsExactly( "127.0.0.2" );
    }

    @Test
    void deprecatedPortConfig()
    {
        RunModeSupport.set( RunMode.PROD );
        when( this.config.http_xp_port() ).thenReturn( 9999 );
        when( this.config.http_monitor_port() ).thenReturn( 8888 );

        configure();

        assertEquals( 9999, getConnector( DispatchConstants.XP_CONNECTOR ).getPort() );
        assertEquals( 8888, getConnector( DispatchConstants.STATUS_CONNECTOR ).getPort() );
    }

    @Test
    void portConfigWinsOverDeprecated()
    {
        RunModeSupport.set( RunMode.PROD );
        when( this.config.http_web_port() ).thenReturn( 7777 );
        when( this.config.http_xp_port() ).thenReturn( 9999 );
        when( this.config.http_statistics_port() ).thenReturn( 6666 );
        when( this.config.http_monitor_port() ).thenReturn( 8888 );

        configure();

        assertEquals( 7777, getConnector( DispatchConstants.XP_CONNECTOR ).getPort() );
        assertEquals( 6666, getConnector( DispatchConstants.STATUS_CONNECTOR ).getPort() );
    }

    private ServerConnector getConnector( final String name )
    {
        return (ServerConnector) Arrays.stream( this.object.getConnectors() )
            .filter( connector -> name.equals( connector.getName() ) )
            .findFirst()
            .orElseThrow();
    }

    private List<String> getHosts( final String name )
    {
        return Arrays.stream( this.object.getConnectors() )
            .filter( connector -> name.equals( connector.getName() ) )
            .map( connector -> ( (ServerConnector) connector ).getHost() )
            .toList();
    }
}
