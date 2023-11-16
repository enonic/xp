package com.enonic.xp.image;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.zip.CRC32;
import java.util.zip.CheckedOutputStream;
import java.util.zip.DeflaterOutputStream;

final class ImagePlaceholderFactory
{
    private static final byte[] PNG_MAGIC = {(byte) 137, 80, 78, 71, 13, 10, 26, 10};

    private static final byte[] IDAT_TYPE_BYTES = "IDAT".getBytes( StandardCharsets.ISO_8859_1 );

    private static final byte[] IHDR_TYPE_BYTES = "IHDR".getBytes( StandardCharsets.ISO_8859_1 );

    private static final byte[] IHDR_CONST_PART = {(byte) 8, 6, 0, 0, 0};

    private static final byte[] IEND = {(byte) 0, 0, 0, 0, 73, 69, 78, 68, -82, 66, 96, -126};

    private static final byte[] PREFIX = "data:image/png;base64,".getBytes( StandardCharsets.ISO_8859_1 );

    private final int width;

    private final int height;

    ImagePlaceholderFactory( final int width, final int height )
    {
        this.width = width;
        this.height = height;
    }

    public String create()
    {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try
        {
            output.write( PREFIX );
            try (var base64Stream = Base64.getEncoder().wrap( output ))
            {
                base64Stream.write( PNG_MAGIC );
                writeChunk( base64Stream, IHDR_TYPE_BYTES, createIhdr() );
                writeChunk( base64Stream, IDAT_TYPE_BYTES, createIdat() );
                base64Stream.write( IEND );
            }
        }
        catch ( IOException e )
        {
            throw new UncheckedIOException( e );
        }

        return output.toString( StandardCharsets.ISO_8859_1 );
    }

    private byte[] createIhdr()
        throws IOException
    {
        ByteArrayOutputStream ihdr = new ByteArrayOutputStream();
        writeBigEndianInt( ihdr, width );
        writeBigEndianInt( ihdr, height );
        ihdr.write( IHDR_CONST_PART );
        return ihdr.toByteArray();
    }

    private byte[] createIdat()
        throws IOException
    {
        final ByteArrayOutputStream idat = new ByteArrayOutputStream();

        final int bufferSize = 512; // DeflaterOutputStream uses this size as a default buffer size
        byte[] buffer = new byte[bufferSize];
        int totalBytes = width * height * 5; // 5 bytes per pixel (RGBA)
        try (DeflaterOutputStream defStream = new DeflaterOutputStream( idat ))
        {
            for ( int i = 0; i < totalBytes; i += bufferSize )
            {
                defStream.write( buffer, 0, Math.min( bufferSize, totalBytes - i ) );
            }
        }
        return idat.toByteArray();
    }

    private void writeChunk( final OutputStream outputStream, final byte[] typeBytes, final byte[] data )
        throws IOException
    {
        CheckedOutputStream crcStream = new CheckedOutputStream( outputStream, new CRC32() );
        writeBigEndianInt( outputStream, data.length );
        crcStream.write( typeBytes );
        crcStream.write( data );
        writeBigEndianInt( outputStream, (int) crcStream.getChecksum().getValue() );
    }

    private static void writeBigEndianInt( final OutputStream stream, final int value )
        throws IOException
    {
        stream.write( ( value >>> 24 ) & 0xFF );
        stream.write( ( value >>> 16 ) & 0xFF );
        stream.write( ( value >>> 8 ) & 0xFF );
        stream.write( value & 0xFF );
    }
}
