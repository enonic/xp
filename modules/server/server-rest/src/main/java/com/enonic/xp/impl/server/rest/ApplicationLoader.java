package com.enonic.xp.impl.server.rest;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

import com.google.common.io.ByteSource;

import com.enonic.xp.core.internal.security.MessageDigests;
import com.enonic.xp.event.Event;

public class ApplicationLoader
{
    private static final Set<String> ALLOWED_PROTOCOLS = Set.of( "http", "https" );

    public ByteSource load( final String urlString, final String sha512Hex, final Consumer<Event> eventConsumer )
    {
        final byte[] sha512 = Optional.ofNullable( sha512Hex ).map( HexFormat.of()::parseHex ).orElse( null );
        try
        {
            final URL url = URI.create( urlString ).toURL();
            return load( url, sha512, eventConsumer );
        }
        catch ( MalformedURLException e )
        {
            throw new UncheckedIOException( e );
        }
    }

    public ByteSource load( final URL url, final byte[] sha512Checksum, final Consumer<Event> eventConsumer )
    {
        if ( !ALLOWED_PROTOCOLS.contains( url.getProtocol() ) )
        {
            throw new IllegalArgumentException( "Unsupported protocol: " + url.getProtocol() );
        }

        try
        {
            final URLConnection connection = url.openConnection();

            final InputStream inputStream = connection.getInputStream();
            final DigestInputStream digestInputStream = new DigestInputStream( inputStream, MessageDigests.sha512() );
            final ProgressInputStream progressInputStream =
                new ProgressInputStream( digestInputStream, connection.getContentLengthLong(), url.toString(), eventConsumer );
            try (inputStream; digestInputStream; progressInputStream)
            {
                final byte[] bytes = progressInputStream.readAllBytes();

                if ( sha512Checksum != null && !MessageDigest.isEqual( sha512Checksum, digestInputStream.getMessageDigest().digest() ) )
                {
                    throw new IllegalArgumentException( "Checksum validation failed" );
                }
                return ByteSource.wrap( bytes );
            }
        }
        catch ( IOException e )
        {
            throw new UncheckedIOException( "Failed to load application from " + url, e );
        }
    }

    private static class ProgressInputStream
        extends FilterInputStream
    {
        private final long totalLength;

        private final String message;

        private final Consumer<Event> eventConsumer;

        private long read;

        private int lastPct = -1;

        ProgressInputStream( final InputStream in, final long totalLength, final String message, Consumer<Event> eventConsumer )
        {
            super( in );
            this.totalLength = totalLength;
            this.message = message;
            this.eventConsumer = eventConsumer;
        }

        @Override
        public int read()
            throws IOException
        {
            return processReadResult( in.read(), 1 );
        }

        @Override
        public int read( final byte[] b, final int off, final int len )
            throws IOException
        {
            final int read = in.read( b, off, len );
            return processReadResult( read, read );
        }

        private int processReadResult( final int result, final int increment )
        {
            if ( result != -1 )
            {
                read += increment;
                reportProgress();
            }
            return result;
        }

        private void reportProgress()
        {
            int currentPct = totalLength > 0 ? (int) Math.min( 100, Math.round( read * 100. / totalLength ) ) : 0;
            if ( lastPct != currentPct )
            {
                eventConsumer.accept( progress( message, currentPct ) );
                lastPct = currentPct;
            }
        }
    }

    public static Event progress( final String url, final int progress )
    {
        return Event.create( "application" )
            .distributed( false )
            .value( "eventType", "PROGRESS" )
            .value( "applicationUrl", url )
            .value( "progress", progress )
            .build();
    }
}
