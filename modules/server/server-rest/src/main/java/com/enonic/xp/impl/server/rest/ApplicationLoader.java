package com.enonic.xp.impl.server.rest;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.Optional;
import java.util.function.Consumer;

import com.google.common.base.Preconditions;
import com.google.common.io.ByteSource;
import com.google.common.io.ByteStreams;

import com.enonic.xp.core.internal.security.MessageDigests;
import com.enonic.xp.event.Event;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebException;

public class ApplicationLoader
{
    private final UrlAllowList allowList;

    private final boolean requireChecksum;

    private final long maxSize;

    private final int maxRedirects;

    private final int connectTimeoutMillis;

    private final int readTimeoutMillis;

    ApplicationLoader( final String allowedUrls, final boolean requireChecksum, final long maxSize, final int maxRedirects,
                       final int connectTimeoutMillis, final int readTimeoutMillis )
    {
        Preconditions.checkArgument( maxSize > 0 && maxSize < Long.MAX_VALUE, "maxSize out of range: %s", maxSize );
        Preconditions.checkArgument( maxRedirects >= 0, "maxRedirects out of range: %s", maxRedirects );
        Preconditions.checkArgument( connectTimeoutMillis > 0, "connectTimeoutMillis out of range: %s", connectTimeoutMillis );
        Preconditions.checkArgument( readTimeoutMillis > 0, "readTimeoutMillis out of range: %s", readTimeoutMillis );
        this.allowList = new UrlAllowList( allowedUrls );
        this.requireChecksum = requireChecksum;
        this.maxSize = maxSize;
        this.maxRedirects = maxRedirects;
        this.connectTimeoutMillis = connectTimeoutMillis;
        this.readTimeoutMillis = readTimeoutMillis;
    }

    public ByteSource load( final String urlString, final String sha512Hex, final Consumer<Event> eventConsumer )
    {
        if ( !allowList.matches( urlString ) )
        {
            throw new WebException( HttpStatus.CONFLICT, "URL is not in the pull allowlist" );
        }
        if ( requireChecksum && ( sha512Hex == null || sha512Hex.isBlank() ) )
        {
            throw new WebException( HttpStatus.CONFLICT, "SHA512 checksum is required for pull" );
        }
        final byte[] sha512;
        try
        {
            sha512 = Optional.ofNullable( sha512Hex ).map( HexFormat.of()::parseHex ).orElse( null );
        }
        catch ( IllegalArgumentException e )
        {
            throw WebException.badRequest( "Invalid SHA512 checksum", e );
        }
        final URL url;
        try
        {
            url = URI.create( urlString ).toURL();
        }
        catch ( IllegalArgumentException | MalformedURLException e )
        {
            throw WebException.badRequest( "Invalid URL", e );
        }
        return load( url, sha512, eventConsumer );
    }

    private ByteSource load( final URL url, final byte[] sha512Checksum, final Consumer<Event> eventConsumer )
    {
        try
        {
            final URLConnection connection = connect( url );

            if ( connection.getContentLengthLong() > maxSize )
            {
                throw tooLarge();
            }

            final InputStream inputStream = ByteStreams.limit( connection.getInputStream(), maxSize + 1 );
            final DigestInputStream digestInputStream = new DigestInputStream( inputStream, MessageDigests.sha512() );
            final ProgressInputStream progressInputStream =
                new ProgressInputStream( digestInputStream, connection.getContentLengthLong(), url.toString(), eventConsumer );
            try (inputStream; digestInputStream; progressInputStream)
            {
                final byte[] bytes = progressInputStream.readAllBytes();

                if ( bytes.length > maxSize )
                {
                    throw tooLarge();
                }

                if ( sha512Checksum != null && !MessageDigest.isEqual( sha512Checksum, digestInputStream.getMessageDigest().digest() ) )
                {
                    throw WebException.badRequest( "Checksum validation failed" );
                }
                return ByteSource.wrap( bytes );
            }
        }
        catch ( IOException e )
        {
            throw new UncheckedIOException( "Failed to load application from " + url, e );
        }
    }

    private URLConnection connect( final URL start )
        throws IOException
    {
        URL url = start;
        for ( int redirects = 0; ; redirects++ )
        {
            final URLConnection connection = url.openConnection();
            connection.setConnectTimeout( connectTimeoutMillis );
            connection.setReadTimeout( readTimeoutMillis );

            if ( !( connection instanceof HttpURLConnection http ) )
            {
                return connection;
            }

            http.setInstanceFollowRedirects( false );
            final String location = isRedirect( http.getResponseCode() ) ? http.getHeaderField( "Location" ) : null;
            if ( location == null )
            {
                return http;
            }

            http.disconnect();
            if ( redirects >= maxRedirects )
            {
                throw WebException.badRequest( "Too many redirects from " + start );
            }

            url = resolveRedirect( url, location );
            if ( !allowList.matches( url.toString() ) )
            {
                throw new WebException( HttpStatus.CONFLICT, "Redirect target is not in the pull allowlist" );
            }
        }
    }

    private static boolean isRedirect( final int status )
    {
        return status == HttpURLConnection.HTTP_MOVED_PERM || status == HttpURLConnection.HTTP_MOVED_TEMP ||
            status == HttpURLConnection.HTTP_SEE_OTHER || status == 307 || status == 308;
    }

    private static URL resolveRedirect( final URL from, final String location )
    {
        try
        {
            return from.toURI().resolve( location ).toURL();
        }
        catch ( URISyntaxException | IllegalArgumentException | MalformedURLException e )
        {
            throw WebException.badRequest( "Invalid redirect location: " + location, e );
        }
    }

    private WebException tooLarge()
    {
        return WebException.badRequest( String.format( "Application exceeds the maximum size of %d bytes", maxSize ) );
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
