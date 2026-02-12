package com.enonic.xp.server.udc.impl;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class PingSender
    implements Runnable
{
    private static final Logger LOG = LoggerFactory.getLogger( PingSender.class );

    private static final long HTTP_TIMEOUT = TimeUnit.SECONDS.toMillis( 20 );

    private final UdcInfoGenerator generator;

    private final String url;

    PingSender( final UdcInfoGenerator generator, final String url )
    {
        this.generator = generator;
        this.url = url;
    }

    @Override
    public void run()
    {
        final UdcInfo info = this.generator.generate();
        send( info.toJson() );
    }

    private void send( final String body )
    {
        final HttpURLConnection conn;
        final int status;
        try
        {
            conn = (HttpURLConnection) URI.create( this.url ).toURL().openConnection();
            try
            {
                conn.setDoOutput( true );
                conn.setConnectTimeout( (int) HTTP_TIMEOUT );
                conn.setReadTimeout( (int) HTTP_TIMEOUT );
                conn.setRequestMethod( "POST" );

                conn.getOutputStream().write( body.getBytes( StandardCharsets.UTF_8 ) );

                status = conn.getResponseCode();
            }
            finally
            {
                conn.disconnect();
            }
        }
        catch ( IOException e )
        {
            throw new UncheckedIOException( e );
        }
        if ( status >= 300 )
        {
            LOG.debug( "UDC ping failed. Responding with code {}.", status );
        }
    }
}
