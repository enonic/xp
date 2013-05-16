package com.enonic.wem.web.servlet;

import javax.servlet.http.HttpServletRequest;

import com.google.common.base.Strings;

public final class ServletRequestUrlHelper
{
    public static String createUrl( final String path )
    {
        return createUrl( ServletRequestHolder.getRequest(), path );
    }

    public static String createUrl( final HttpServletRequest req, final String path )
    {
        final StringBuilder str = new StringBuilder();

        str.append( req.getScheme() ).append( "://" );
        str.append( req.getServerName() );

        if ( includePortNumber( req ) )
        {
            str.append( ":" ).append( req.getLocalPort() );
        }

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

    private static boolean includePortNumber( final HttpServletRequest req )
    {
        final String scheme = req.getScheme();
        final int port = req.getLocalPort();

        if ( scheme.equals( "http" ) && ( port == 80 ) )
        {
            return false;
        }

        return true;
    }
}
