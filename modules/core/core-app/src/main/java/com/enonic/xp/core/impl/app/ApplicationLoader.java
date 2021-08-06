package com.enonic.xp.core.impl.app;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.URL;
import java.net.URLConnection;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.util.function.Consumer;

import com.google.common.io.ByteSource;

import com.enonic.xp.core.impl.app.event.ApplicationEvents;
import com.enonic.xp.core.internal.security.MessageDigests;
import com.enonic.xp.event.Event;

public class ApplicationLoader
{
    private final Consumer<Event> eventConsumer;

    public ApplicationLoader( final Consumer<Event> eventConsumer )
    {
        this.eventConsumer = eventConsumer;
    }

    public ByteSource load( final URL url, final byte[] sha512Checksum )
    {
        try
        {
            final URLConnection connection = url.openConnection();

            final InputStream inputStream = connection.getInputStream();
            final DigestInputStream digestInputStream = new DigestInputStream( inputStream, MessageDigests.sha512() );
            final ProgressInputStream progressInputStream =
                new ProgressInputStream( digestInputStream, connection.getContentLengthLong(), url.toString() );
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

    private class ProgressInputStream
        extends FilterInputStream
    {
        private final long totalLength;

        private final String message;

        private long read;

        private int lastPct = -1;

        ProgressInputStream( final InputStream in, final long totalLength, final String message )
        {
            super( in );
            this.totalLength = totalLength;
            this.message = message;
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
                eventConsumer.accept( ApplicationEvents.progress( message, currentPct ) );
                lastPct = currentPct;
            }
        }
    }
}
