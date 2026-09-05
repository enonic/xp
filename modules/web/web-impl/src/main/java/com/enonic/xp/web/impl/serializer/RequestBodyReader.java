package com.enonic.xp.web.impl.serializer;

import java.io.IOException;
import java.io.Reader;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;

import com.google.common.net.MediaType;

import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebException;

public final class RequestBodyReader
{
    private static final List<MediaType> TEXT_CONTENT_TYPES =
        List.of( MediaType.ANY_TEXT_TYPE, MediaType.JSON_UTF_8.withoutParameters() );

    private static final int BUFFER_SIZE = 8192;

    private RequestBodyReader()
    {
    }

    public static Object readBody( final HttpServletRequest req, final long maxBytes )
        throws IOException
    {
        final String type = req.getContentType();
        if ( type == null || !isText( MediaType.parse( type ) ) )
        {
            return null;
        }

        if ( req.getContentLengthLong() > maxBytes )
        {
            throw tooLarge( maxBytes );
        }

        final StringBuilder body = new StringBuilder();
        final char[] buffer = new char[BUFFER_SIZE];
        final Reader reader = req.getReader();
        long total = 0;
        int read;
        while ( ( read = reader.read( buffer ) ) != -1 )
        {
            total += utf8Length( buffer, read );
            if ( total > maxBytes )
            {
                throw tooLarge( maxBytes );
            }
            body.append( buffer, 0, read );
        }
        return body.toString();
    }

    public static boolean isText( final MediaType type )
    {
        return TEXT_CONTENT_TYPES.stream().anyMatch( type::is );
    }

    private static long utf8Length( final char[] chars, final int length )
    {
        long bytes = 0;
        for ( int i = 0; i < length; i++ )
        {
            final char c = chars[i];
            if ( c < 0x80 )
            {
                bytes += 1;
            }
            else if ( c < 0x800 || Character.isSurrogate( c ) )
            {
                bytes += 2;
            }
            else
            {
                bytes += 3;
            }
        }
        return bytes;
    }

    private static WebException tooLarge( final long maxBytes )
    {
        return new WebException( HttpStatus.PAYLOAD_TOO_LARGE, "Request body exceeds the maximum of " + maxBytes + " bytes" );
    }
}
