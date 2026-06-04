package com.enonic.xp.web.jetty.impl.configurator;

import org.eclipse.jetty.server.HttpConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.web.dispatch.DispatchConstants;

public final class HttpConfigurator
    extends ConnectorConfigurator
{
    private static final Logger LOG = LoggerFactory.getLogger( HttpConfigurator.class );

    private static final int UNSET_PORT = -1;

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

        final int webPort =
            effectivePort( this.config.http_web_port(), this.config.http_xp_port(), "http.web.port", "http.xp.port", 8080 );
        addConnectors( factory, DispatchConstants.XP_CONNECTOR, webPort, this.config.http_web_host() );

        addConnectors( factory, DispatchConstants.API_CONNECTOR, this.config.http_management_port(),
                       this.config.http_management_host() );

        final int statisticsPort =
            effectivePort( this.config.http_statistics_port(), this.config.http_monitor_port(), "http.statistics.port",
                           "http.monitor.port", 2609 );
        addConnectors( factory, DispatchConstants.STATUS_CONNECTOR, statisticsPort, this.config.http_statistics_host() );
    }

    private static int effectivePort( final int port, final int deprecatedPort, final String name, final String deprecatedName,
                                      final int defaultPort )
    {
        if ( port != UNSET_PORT )
        {
            return port;
        }
        if ( deprecatedPort != UNSET_PORT )
        {
            LOG.warn( "Configuration property [{}] is deprecated. Use [{}] instead", deprecatedName, name );
            return deprecatedPort;
        }
        return defaultPort;
    }
}
