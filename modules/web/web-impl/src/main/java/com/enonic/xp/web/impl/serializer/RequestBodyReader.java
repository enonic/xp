package com.enonic.xp.web.impl.serializer;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;

import com.google.common.io.CharStreams;
import com.google.common.net.MediaType;

public final class RequestBodyReader
{
    private static final List<MediaType> TEXT_CONTENT_TYPES =
        List.of( MediaType.ANY_TEXT_TYPE, MediaType.JSON_UTF_8.withoutParameters() );

    public static Object readBody( final HttpServletRequest req )
        throws IOException
    {
        final String type = req.getContentType();
        if ( type == null )
        {
            return null;
        }

        return isText( MediaType.parse( type ) ) ? CharStreams.toString( req.getReader() ) : null;
    }

    public static boolean isText( final MediaType type )
    {
        return TEXT_CONTENT_TYPES.stream().anyMatch( type::is );
    }
}
