package com.enonic.xp.web.jetty.impl.configurator;

import org.eclipse.jetty.server.NCSARequestLog;
import org.eclipse.jetty.server.Server;

public final class RequestLogConfigurator
    extends JettyConfigurator<Server>
{
    @Override
    protected void doConfigure()
    {
        final NCSARequestLog log = new NCSARequestLog();
        log.setAppend( true );
        log.setExtended( true );
        log.setLogTimeZone( "GMT" );
        log.setRetainDays( 31 );
    }
}
