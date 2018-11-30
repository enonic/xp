package com.enonic.xp.web.jetty.impl.configurator;

import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.ServerConnector;

public final class HttpConfigurator
    extends ConnectorConfigurator
{
    @Override
    protected void doConfigure()
    {
        if ( !this.config.http_enabled() )
        {
            return;
        }

        final HttpConnectionFactory factory = new HttpConnectionFactory();
        doConfigure( factory );

        final ServerConnector connector = new ServerConnector( this.object, factory );
        connector.setName( "xp" );
        doConfigure( connector, this.config.http_port() );

        final ServerConnector connectorApi = new ServerConnector( this.object, factory );
        connectorApi.setName( "api" );
        doConfigure( connectorApi, this.config.http_api_port() );

        this.object.addConnector( connector );
        this.object.addConnector( connectorApi );
    }
}
