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
            total += read;
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

    private static WebException tooLarge( final long maxBytes )
    {
        return new WebException( HttpStatus.PAYLOAD_TOO_LARGE, "Request body exceeds the maximum of " + maxBytes + " bytes" );
    }
}
