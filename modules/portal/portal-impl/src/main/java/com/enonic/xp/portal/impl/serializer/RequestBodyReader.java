package com.enonic.xp.portal.impl.serializer;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import com.google.common.io.CharStreams;
import com.google.common.net.MediaType;

public final class RequestBodyReader
{
    private final static MediaType JSON = MediaType.JSON_UTF_8.withoutParameters();

    public static Object readBody( final HttpServletRequest req )
        throws IOException
    {
        final String type = req.getContentType();
        if ( type == null )
        {
            return null;
        }

        return readBody( req, MediaType.parse( type ) );
    }

    private static Object readBody( final HttpServletRequest req, final MediaType type )
        throws IOException
    {
        if ( !isText( type ) )
        {
            return null;
        }

        return CharStreams.toString( req.getReader() );
    }

    public static boolean isText( final MediaType type )
    {
        return type.type().equals( "text" ) || isJson( type );
    }

    public static boolean isJson( final MediaType type )
    {
        return type.is( JSON );
    }
}
