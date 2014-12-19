package com.enonic.xp.web.servlet;

import javax.servlet.http.HttpServletRequest;

import com.google.common.base.Strings;

public final class ServletRequestUrlHelper
{
    private ServletRequestUrlHelper()
    {
    }

    public static String createUri( final String path )
    {
        return createUri( ServletRequestHolder.getRequest(), path );
    }

    public static String createUri( final HttpServletRequest req, final String path )
    {
        final StringBuilder str = new StringBuilder();

        final String contextPath = req.getContextPath();
        if ( !Strings.isNullOrEmpty( contextPath ) )
        {
            str.append( contextPath );
        }

        if ( !Strings.isNullOrEmpty( path ) )
        {
            if ( !path.startsWith( "/" ) )
            {
                str.append( "/" );
            }

            str.append( path );
        }

        return str.toString();
    }

    public static String createUriWithHost( final String path )
    {
        return createUriWithHost( ServletRequestHolder.getRequest(), path );
    }

    public static String createUriWithHost( final HttpServletRequest req, final String path )
    {
        final StringBuilder str = new StringBuilder();
        str.append( req.getScheme() ).append( "://" );
        str.append( req.getServerName() );

        if ( req.getServerPort() != 80 )
        {
            str.append( ":" ).append( req.getServerPort() );
        }

        str.append( createUri( req, path ) );
        return str.toString();
    }
}
