package com.enonic.xp.web.jetty.impl.configurator;

import java.util.Objects;

import org.eclipse.jetty.server.CustomRequestLog;
import org.eclipse.jetty.server.RequestLogWriter;
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

        final String fileName = Objects.requireNonNullElseGet( this.config.log_file(), () -> HomeDir.get()
            .toPath()
            .resolve( "logs" )
            .resolve( "jetty-yyyy_mm_dd.request.log" )
            .toAbsolutePath()
            .toString() );

        final RequestLogWriter requestLogWriter = new RequestLogWriter( fileName );
        requestLogWriter.setAppend( this.config.log_append() );
        requestLogWriter.setTimeZone( this.config.log_timeZone() );
        requestLogWriter.setRetainDays( this.config.log_retainDays() );

        final String format = this.config.log_extended() ? CustomRequestLog.EXTENDED_NCSA_FORMAT : CustomRequestLog.NCSA_FORMAT;

        this.object.setRequestLog( new CustomRequestLog( requestLogWriter, format ) );
    }
}
