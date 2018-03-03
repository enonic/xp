package com.enonic.xp.xml.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;

import org.apache.commons.io.ByteOrderMark;

import com.google.common.io.CharSource;

final class ByteOrderMarkHelper
{
    private final static int READ_AHEAD_LENGTH = 2;

    private final static ByteOrderMark[] BOMS = {ByteOrderMark.UTF_8, ByteOrderMark.UTF_16BE, ByteOrderMark.UTF_16LE};

    public static Reader openStreamSkippingBOM( final CharSource source )
        throws IOException
    {
        final BufferedReader sourceStream = source.openBufferedStream();
        sourceStream.mark( READ_AHEAD_LENGTH );

        // read prefix from source
        final char[] dataPrefix = new char[READ_AHEAD_LENGTH];
        final int r = sourceStream.read( dataPrefix, 0, READ_AHEAD_LENGTH );
        if ( r < READ_AHEAD_LENGTH )
        {
            sourceStream.reset();
            return sourceStream;
        }

        // check if prefix matches any byte order mark
        for ( ByteOrderMark bom : BOMS )
        {
            // prefix chars to bytes
            final CharBuffer charBuffer = CharBuffer.wrap( dataPrefix );
            final ByteBuffer byteBuffer = Charset.forName( bom.getCharsetName() ).encode( charBuffer );
            final byte[] prefixBytes = byteBuffer.array();

            if ( isBom( bom, prefixBytes ) )
            {
                // bom found, skip from char source
                sourceStream.reset();
                sourceStream.skip( bom.length() / 2 );
                sourceStream.mark( 0 );
                return sourceStream;
            }
        }

        sourceStream.reset();
        return sourceStream;
    }

    private static boolean isBom( final ByteOrderMark bom, final byte[] bytes )
    {
        final byte[] bomBytes = bom.getBytes();
        if ( bytes.length < bomBytes.length )
        {
            return false;
        }
        for ( int i = 0; i < bomBytes.length; i++ )
        {
            if ( bomBytes[i] != bytes[i] )
            {
                return false;
            }
        }
        return true;
    }

}