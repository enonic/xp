package com.enonic.xp.portal.impl.serializer;

import javax.servlet.http.HttpServletRequest;

import com.google.common.io.CharStreams;
import com.google.common.net.MediaType;

public final class RequestBodyReader
{
    private final static MediaType JSON = MediaType.JSON_UTF_8.withoutParameters();

    public static Object readBody( final HttpServletRequest req )
        throws Exception
    {
        final String type = req.getContentType();
        if ( ( type == null ) || !isText( MediaType.parse( type ) ) )
        {
            return null;
        }

        return CharStreams.toString( req.getReader() );
    }

    public static boolean isText( final MediaType type )
    {
        return type.is( MediaType.ANY_TEXT_TYPE ) || type.is( JSON );
    }
}
