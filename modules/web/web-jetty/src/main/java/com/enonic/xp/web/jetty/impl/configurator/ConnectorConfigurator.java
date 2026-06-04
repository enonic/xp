package com.enonic.xp.web.jetty.impl.configurator;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.List;

import org.eclipse.jetty.server.ForwardedRequestCustomizer;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;

import com.enonic.xp.core.internal.net.NetworkInterfaceResolver;
import com.enonic.xp.server.RunMode;

import static com.google.common.base.Strings.isNullOrEmpty;

public abstract class ConnectorConfigurator
    extends JettyConfigurator<Server>
{
    private final NetworkInterfaceResolver resolver =
        new NetworkInterfaceResolver( () -> RunMode.isDev() ? "_local_" : "0.0.0.0" );

    protected final void addConnectors( final HttpConnectionFactory factory, final String name, final int port, final String host )
    {
        for ( final String address : resolveHosts( host ) )
        {
            final ServerConnector connector = new ServerConnector( this.object, factory );
            connector.setName( name );
            connector.setPort( port );
            connector.setHost( address );
            connector.setIdleTimeout( this.config.timeout() );
            this.object.addConnector( connector );
        }
    }

    protected final void doConfigure( final HttpConnectionFactory factory )
    {
        factory.getHttpConfiguration().addCustomizer( new ForwardedRequestCustomizer() );

        final HttpConfiguration config = factory.getHttpConfiguration();

        // HTTP/1.1 requires Date header if possible
        config.setSendDateHeader( true );
        config.setSendServerVersion( this.config.sendServerHeader() );
        config.setSendXPoweredBy( this.config.sendServerHeader() );
        config.setRequestHeaderSize( this.config.http_requestHeaderSize() );
        config.setResponseHeaderSize( this.config.http_responseHeaderSize() );
    }

    private List<String> resolveHosts( final String connectorHost )
    {
        final String host = isNullOrEmpty( connectorHost ) ? this.config.host() : connectorHost;
        final List<String> resolved = resolver.resolveAddresses( host );
        // a single connector with null host binds all interfaces
        return resolved.stream().anyMatch( ConnectorConfigurator::isAnyLocalAddress ) ? Collections.singletonList( null ) : resolved;
    }

    private static boolean isAnyLocalAddress( final String address )
    {
        try
        {
            return InetAddress.getByName( address ).isAnyLocalAddress();
        }
        catch ( UnknownHostException e )
        {
            return false;
        }
    }
}
