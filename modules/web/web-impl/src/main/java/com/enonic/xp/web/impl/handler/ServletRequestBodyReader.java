package com.enonic.xp.web.impl.handler;

import java.io.IOException;

import javax.servlet.ServletRequest;

import com.google.common.io.CharStreams;
import com.google.common.net.MediaType;

import com.enonic.xp.util.Exceptions;

public final class ServletRequestBodyReader
{
    private final static MediaType JSON = MediaType.JSON_UTF_8.withoutParameters();

    public static Object readBody( final ServletRequest servletRequest )
    {
        final String contentType = servletRequest.getContentType();
        return contentType == null ? null : readBody( servletRequest, MediaType.parse( contentType ) );
    }

    private static Object readBody( final ServletRequest req, final MediaType type )
    {
        try
        {
            return isText( type ) ? CharStreams.toString( req.getReader() ) : null;
        }
        catch ( IOException e )
        {
            throw Exceptions.unchecked( e );
        }
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
