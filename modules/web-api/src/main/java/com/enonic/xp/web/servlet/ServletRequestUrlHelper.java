package com.enonic.xp.web.servlet;

import javax.servlet.http.HttpServletRequest;

import com.google.common.annotations.Beta;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.net.HostAndPort;
import com.google.common.net.HttpHeaders;

import com.enonic.xp.web.vhost.VirtualHost;
import com.enonic.xp.web.vhost.VirtualHostHelper;

@Beta
public final class ServletRequestUrlHelper
{
    public static final String X_FORWARDED_PROTO = HttpHeaders.X_FORWARDED_PROTO;

    public static final String X_FORWARDED_HOST = "X-Forwarded-Host";

    public static String createUri( final String path )
    {
        return createUri( ServletRequestHolder.getRequest(), path );
    }

    public static String createUri( final HttpServletRequest req, final String path )
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

    public static String getScheme()
    {
        return getScheme( ServletRequestHolder.getRequest() );
    }

    public static String getScheme( final HttpServletRequest req )
    {
        String scheme = req.getHeader( X_FORWARDED_PROTO );
        if ( scheme == null )
        {
            scheme = req.getScheme();
        }
        return scheme;
    }

    public static String getHost()
    {
        return getHost( ServletRequestHolder.getRequest() );
    }

    public static String getHost( final HttpServletRequest req )
    {
        return getHostAndPort( req ).getHostText();
    }

    public static int getPort()
    {
        return getPort( ServletRequestHolder.getRequest() );
    }

    public static int getPort( final HttpServletRequest req )
    {
        return getHostAndPort( req ).getPort();
    }

    public static HostAndPort getHostAndPort()
    {
        return getHostAndPort( ServletRequestHolder.getRequest() );
    }

    public static HostAndPort getHostAndPort( final HttpServletRequest req )
    {
        final String xForwardedHost = req.getHeader( X_FORWARDED_HOST );
        if ( xForwardedHost != null )
        {
            return HostAndPort.fromString( xForwardedHost );
        }

        return HostAndPort.fromParts( req.getServerName(), req.getServerPort() );
    }

    public static String getPath()
    {
        return getPath( ServletRequestHolder.getRequest() );
    }

    public static String getPath( final HttpServletRequest req )
    {
        return createUri( req.getRequestURI() );
    }

    private static String getQueryString( final HttpServletRequest req )
    {
        return req.getQueryString();
    }

    public static String getServerUrl()
    {
        return getServerUrl( ServletRequestHolder.getRequest() );
    }

    public static String getServerUrl( final HttpServletRequest req )
    {
        final StringBuilder str = new StringBuilder();

        //Appends the scheme part
        String scheme = getScheme( req );
        str.append( scheme );

        //Appends the host
        str.append( "://" ).append( getHost( req ) );

        //Appends the port if necessary
        final int port = getPort( req );
        if ( needPortNumber( scheme, port ) )
        {
            str.append( ":" ).append( port );
        }

        return str.toString();
    }

    public static String getFullUrl()
    {
        return getFullUrl( ServletRequestHolder.getRequest() );
    }

    public static String getFullUrl( final HttpServletRequest req )
    {
        //Appends the server part
        StringBuffer fullUrl = new StringBuffer( getServerUrl( req ) );

        //Appends the path part
        fullUrl.append( getPath( req ) );

        //Appends the query string part
        final String queryString = getQueryString( req );
        if ( queryString != null )
        {
            fullUrl.append( "?" ).append( queryString );
        }

        return fullUrl.toString();
    }

    private static boolean needPortNumber( final String scheme, final int port )
    {
        final boolean isHttp = "http".equals( scheme ) && ( 80 == port );
        final boolean isHttps = "https".equals( scheme ) && ( 443 == port );
        return !( isHttp || isHttps );
    }

    public static UriRewritingResult rewriteUri( final String uri )
    {
        return rewriteUri( ServletRequestHolder.getRequest(), uri );
    }

    public static UriRewritingResult rewriteUri( final HttpServletRequest req, final String uri )
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
            outOfScope( true ).
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
