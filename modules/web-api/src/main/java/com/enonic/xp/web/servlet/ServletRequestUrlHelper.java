package com.enonic.xp.web.servlet;

import javax.servlet.http.HttpServletRequest;

import com.google.common.annotations.Beta;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;

import com.enonic.xp.web.vhost.VirtualHost;
import com.enonic.xp.web.vhost.VirtualHostHelper;

@Beta
public final class ServletRequestUrlHelper
{
    private ServletRequestUrlHelper()
    {
    }

    public static String createUri( final String path )
    {
        return createUri( ServletRequestHolder.getRequest(), path );
    }

    private static String createUri( final HttpServletRequest req, final String path )
    {
        final StringBuilder str = new StringBuilder();

        if ( !Strings.isNullOrEmpty( path ) )
        {
            if ( !path.startsWith( "/" ) )
            {
                str.append( "/" );
            }

            str.append( path );
        }
        else
        {
            str.append( "/" );
        }

        return rewriteUri( req, str.toString() ).getRewrittenUri();
    }

    public static String createUriWithHost( final String path )
    {
        return createUriWithHost( ServletRequestHolder.getRequest(), path );
    }

    private static String createUriWithHost( final HttpServletRequest req, final String path )
    {
        final String scheme = req.getScheme();
        final int port = req.getServerPort();

        final StringBuilder str = new StringBuilder();
        str.append( scheme ).append( "://" );
        str.append( req.getServerName() );

        if ( needPortNumber( scheme, port ) )
        {
            str.append( ":" ).append( port );
        }

        str.append( createUri( path ) );
        return str.toString();
    }

    private static boolean needPortNumber( final String scheme, final int port )
    {
        final boolean isHttp = "http".equals( scheme ) && ( port == 80 );
        final boolean isHttps = "https".equals( scheme ) && ( port == 443 );
        return !( isHttp || isHttps );
    }

    public static UriRewritingResult rewriteUri( final String uri )
    {
        return rewriteUri( ServletRequestHolder.getRequest(), uri );
    }

    private static UriRewritingResult rewriteUri( final HttpServletRequest req, final String uri )
    {
        UriRewritingResult.Builder resultBuilder = UriRewritingResult.create().
            rewrittenUri( uri );
        if ( req == null )
        {
            return resultBuilder.build();
        }

        final VirtualHost vhost = VirtualHostHelper.getVirtualHost( req );
        if ( vhost == null )
        {
            return resultBuilder.build();
        }

        final String targetPath = vhost.getTarget();
        if ( uri.startsWith( targetPath ) )
        {
            final String result = uri.substring( targetPath.length() );
            final String newUri = normalizePath( vhost.getSource() + "/" + result );
            return resultBuilder.rewrittenUri( newUri ).
                deletedUriPrefix( targetPath ).
                newUriPrefix( normalizePath( vhost.getSource() ) ).
                build();
        }

        return resultBuilder.
            rewrittenUri( normalizePath( uri ) ).
            build();
    }

    private static String normalizePath( final String value )
    {
        if ( Strings.isNullOrEmpty( value ) )
        {
            return "/";
        }

        final Iterable<String> parts = Splitter.on( '/' ).trimResults().omitEmptyStrings().split( value );
        return "/" + Joiner.on( '/' ).join( parts );
    }

}
