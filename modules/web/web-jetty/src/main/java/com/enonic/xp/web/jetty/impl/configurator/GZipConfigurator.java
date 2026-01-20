package com.enonic.xp.web.jetty.impl.configurator;

import org.eclipse.jetty.compression.gzip.GzipCompression;
import org.eclipse.jetty.compression.server.CompressionConfig;
import org.eclipse.jetty.compression.server.CompressionHandler;
import org.eclipse.jetty.ee11.servlet.ServletContextHandler;

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
        GzipCompression gzip = new GzipCompression();
        gzip.setMinCompressSize( this.config.gzip_minSize() );
        CompressionConfig cfg = CompressionConfig.builder().defaults().compressExcludeMimeType( "application/octet-stream" ).build();
        CompressionHandler handler = new CompressionHandler();
        handler.putCompression( gzip );
        handler.putConfiguration( "/", cfg );

        this.object.insertHandler( handler );
    }
}
