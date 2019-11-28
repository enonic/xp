package com.enonic.xp.server.udc.impl;

import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class PingSenderImpl
    implements PingSender
{
    private final static Logger LOG = LoggerFactory.getLogger( PingSenderImpl.class );

    private final static long HTTP_TIMEOUT = TimeUnit.SECONDS.toMillis( 20 );

    private final UdcInfoGenerator generator;

    private final String url;

    PingSenderImpl( final UdcInfoGenerator generator, final String url )
    {
        this.generator = generator;
        this.url = url;
    }

    @Override
    public void send()
    {
        try
        {
            final UdcInfo info = this.generator.generate();
            send( info.toJson() );
        }
        catch ( final Exception e )
        {
            LOG.debug( "Failed to send UDC ping", e );
        }
    }

    private void send( final String body )
        throws Exception
    {
        final HttpURLConnection conn = (HttpURLConnection) new URL( this.url ).openConnection();
        conn.setDoOutput( true );
        conn.setConnectTimeout( (int) HTTP_TIMEOUT );
        conn.setReadTimeout( (int) HTTP_TIMEOUT );
        conn.setRequestMethod( "POST" );

        conn.getOutputStream().write( body.getBytes( StandardCharsets.UTF_8 ) );

        final int status = conn.getResponseCode();
        if ( status >= 300 )
        {
            LOG.debug( "UDC ping failed. Responding with code {}.", status );
            return;
        }

        conn.disconnect();
    }
}
