package com.enonic.xp.xml.parser;

import java.io.IOException;
import java.io.PushbackReader;
import java.io.Reader;

import com.google.common.io.CharSource;

final class ByteOrderMarkHelper
{
    private static final int UTF_8_BOM = '\uFEFF';

    private static final int EOF = -1;

    public static Reader openStreamSkippingBOM( final CharSource source )
        throws IOException
    {
        final Reader originalStream = source.openBufferedStream();
        final PushbackReader pushbackReader = new PushbackReader( originalStream );

        final int read = pushbackReader.read();
        if ( read == EOF || read == UTF_8_BOM )
        {
            return pushbackReader;
        }

        pushbackReader.unread( read );
        return pushbackReader;
    }
}