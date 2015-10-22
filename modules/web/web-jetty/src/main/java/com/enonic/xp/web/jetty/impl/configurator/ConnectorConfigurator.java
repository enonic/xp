package com.enonic.xp.web.jetty.impl.configurator;

import org.eclipse.jetty.server.ForwardedRequestCustomizer;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;

import com.google.common.base.Strings;

public abstract class ConnectorConfigurator
    extends JettyConfigurator<Server>
{
    protected final void doConfigure( final ServerConnector connector, final int port )
    {
        connector.setPort( port );
        connector.setHost( getHost() );
        connector.setIdleTimeout( this.config.timeout() );
    }

    protected final void doConfigure( final HttpConnectionFactory factory )
    {
        factory.getHttpConfiguration().addCustomizer( new ForwardedRequestCustomizer() );

        final HttpConfiguration config = factory.getHttpConfiguration();

        // HTTP/1.1 requires Date header if possible
        config.setSendDateHeader( true );
        config.setSendServerVersion( this.config.sendServerHeader() );
        config.setSendXPoweredBy( this.config.sendServerHeader() );
    }

    private String getHost()
    {
        return Strings.emptyToNull( this.config.host() );
    }
}
