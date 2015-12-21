package com.enonic.xp.server.udc.impl;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class PingSenderImpl
    implements PingSender
{
    private final static Logger LOG = LoggerFactory.getLogger( PingSenderImpl.class );

    private final static long HTTP_TIMEOUT = TimeUnit.SECONDS.toMillis( 5 );

    private final UdcInfoGenerator generator;

    private final UdcUrlBuilder urlBuilder;

    public PingSenderImpl( final UdcInfoGenerator generator, final UdcUrlBuilder urlBuilder )
    {
        this.generator = generator;
        this.urlBuilder = urlBuilder;
    }

    @Override
    public void send()
    {
        try
        {
            final UdcInfo info = this.generator.generate();
            final URL url = this.urlBuilder.build( info );
            send( url );
        }
        catch ( final Exception e )
        {
            LOG.error( "Failed to send UDC ping", e );
        }
    }

    private void send( final URL url )
        throws Exception
    {
        final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout( (int) HTTP_TIMEOUT );
        conn.setReadTimeout( (int) HTTP_TIMEOUT );

        final int status = conn.getResponseCode();
        if ( status >= 300 )
        {
            LOG.debug( "UDC ping failed. Responding with code {}.", status );
            return;
        }

        conn.getInputStream().close();
    }
}
