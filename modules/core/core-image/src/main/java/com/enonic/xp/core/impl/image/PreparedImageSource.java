package com.enonic.xp.core.impl.image;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.HexFormat;

import com.google.common.io.ByteSource;

import com.enonic.xp.core.internal.security.MessageDigests;

/** Reads the attachment once, enforcing its byte limit while hashing the exact bytes that will be decoded. */
record PreparedImageSource(Path path, String checksum) implements AutoCloseable
{
    static PreparedImageSource copy( final ByteSource source, final Path folder, final long maxBytes,
                                     final String expectedChecksum ) throws IOException
    {
        Files.createDirectories( folder );
        final Path path = Files.createTempFile( folder, "source-", ".tmp" );
        try
        {
            final MessageDigest digest = MessageDigests.sha512();
            try (var input = source.openStream(); var output = Files.newOutputStream( path ))
            {
                final byte[] buffer = new byte[8192];
                long total = 0;
                while ( true )
                {
                    if ( Thread.currentThread().isInterrupted() ) { throw new IOException( "Image source read interrupted" ); }
                    // Read at most one byte beyond the limit, including for very small configured limits.
                    final int count = input.read( buffer, 0, (int) Math.min( buffer.length, maxBytes - total + ( maxBytes == Long.MAX_VALUE ? 0 : 1 ) ) );
                    if ( count < 0 ) { break; }
                    total += count;
                    if ( total > maxBytes ) { throw new IllegalArgumentException( "Source image exceeds decoding.maxBytes" ); }
                    digest.update( buffer, 0, count );
                    output.write( buffer, 0, count );
                }
            }
            final String checksum = HexFormat.of().formatHex( digest.digest() );
            if ( expectedChecksum != null && !expectedChecksum.equals( checksum ) )
            {
                throw new IllegalStateException( "Attachment checksum mismatch" );
            }
            return new PreparedImageSource( path, checksum );
        }
        catch ( IOException | RuntimeException | Error e )
        {
            Files.deleteIfExists( path );
            throw e;
        }
    }

    @Override
    public void close() throws IOException
    {
        Files.deleteIfExists( path );
    }
}
