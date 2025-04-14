package com.enonic.xp.web.jetty.impl.configurator;

import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.ServerConnector;

import com.enonic.xp.web.dispatch.DispatchConstants;

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
        //OldMetrics.removeAll( InstrumentedConnectionFactory.class );
        final HttpConnectionFactory factory = new HttpConnectionFactory();
        //new InstrumentedConnectionFactory( factory, Metrics.registry() );
        doConfigure( factory );

        final ServerConnector connectorXp = new ServerConnector( this.object, factory );
        connectorXp.setName( DispatchConstants.XP_CONNECTOR );
        doConfigure( connectorXp, this.config.http_xp_port() );

        final ServerConnector connectorApi = new ServerConnector( this.object, factory );
        connectorApi.setName( DispatchConstants.API_CONNECTOR );
        doConfigure( connectorApi, this.config.http_management_port() );

        final ServerConnector connectorStatus = new ServerConnector( this.object, factory );
        connectorStatus.setName( DispatchConstants.STATUS_CONNECTOR );
        doConfigure( connectorStatus, this.config.http_monitor_port() );

        this.object.addConnector( connectorXp );
        this.object.addConnector( connectorApi );
        this.object.addConnector( connectorStatus );
    }
}
