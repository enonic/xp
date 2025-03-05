package com.enonic.xp.web.jetty.impl.configurator;

import org.eclipse.jetty.ee10.servlet.ServletContextHandler;
import org.eclipse.jetty.server.handler.gzip.GzipHandler;

public final class GZipConfigurator
    extends JettyConfigurator<ServletContextHandler>
{
    @Override
    protected void doConfigure()
    {
        if ( !this.config.gzip_enabled() )
        {
            return;
        }

        final GzipHandler handler = new GzipHandler();
        handler.setExcludedAgentPatterns();
        handler.setMinGzipSize( this.config.gzip_minSize() );
        handler.addExcludedMimeTypes( "application/octet-stream" );
        handler.addExcludedMimeTypes( "text/event-stream" );

        this.object.setGzipHandler( handler );
    }
}
