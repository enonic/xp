package com.enonic.xp.web.jetty.impl.configurator;

import java.io.File;

import org.eclipse.jetty.server.NCSARequestLog;
import org.eclipse.jetty.server.Server;

import com.enonic.xp.home.HomeDir;

public final class RequestLogConfigurator
    extends JettyConfigurator<Server>
{
    @Override
    protected void doConfigure()
    {
        if ( !this.config.log_enabled() )
        {
            return;
        }

        final NCSARequestLog log = new NCSARequestLog();
        log.setAppend( this.config.log_append() );
        log.setExtended( this.config.log_extended() );
        log.setLogTimeZone( this.config.log_timeZone() );
        log.setRetainDays( this.config.log_retainDays() );

        final String fileName = this.config.log_file();
        if ( fileName != null )
        {
            log.setFilename( fileName );
        }
        else
        {
            final String pattern = "jetty-yyyy_mm_dd.request.log";
            final File logDir = new File( HomeDir.get().toFile(), "logs" );
            log.setFilename( new File( logDir, pattern ).getAbsolutePath() );
        }

        this.object.setRequestLog( log );
    }
}
