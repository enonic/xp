package com.enonic.xp.web.servlet;

import javax.servlet.http.HttpServletRequest;

import com.google.common.annotations.Beta;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.net.HostAndPort;

import com.enonic.xp.web.vhost.VirtualHost;
import com.enonic.xp.web.vhost.VirtualHostHelper;

@Beta
public final class ServletRequestUrlHelper
{
    public static final String X_FORWARDED_PROTO = "X-Forwarded-Proto";

    public static final String X_FORWARDED_HOST = "X-Forwarded-Host";


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

    public static String getScheme()
    {
        return getScheme( ServletRequestHolder.getRequest() );
    }

    public static String getScheme( final HttpServletRequest httpServletRequest )
    {
        String scheme = httpServletRequest.getHeader( X_FORWARDED_PROTO );
        if ( scheme == null )
        {
            scheme = httpServletRequest.getScheme();
        }
        return scheme;
    }

    public static HostAndPort getHostAndPort( final HttpServletRequest req )
    {
        final String xForwardedHost = req.getHeader( X_FORWARDED_HOST );
        if ( xForwardedHost != null )
        {
            return HostAndPort.fromString( xForwardedHost );
        }
        else
        {
            return HostAndPort.fromParts( req.getServerName(), req.getServerPort() );
        }
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

    public static String getPath()
    {
        return getPath( ServletRequestHolder.getRequest() );
    }

    public static String getPath( final HttpServletRequest httpServletRequest )
    {
        return createUri( httpServletRequest.getRequestURI() );
    }

    private static String getQueryString( final HttpServletRequest httpServletRequest )
    {
        return httpServletRequest.getQueryString();
    }

    public static String getServerUrl()
    {
        return getServerUrl( ServletRequestHolder.getRequest() );
    }

    private static String getServerUrl( final HttpServletRequest httpServletRequest )
    {
        final StringBuilder str = new StringBuilder();

        //Appends the scheme part
        String scheme = getScheme( httpServletRequest );
        str.append( scheme );

        //Appends the host
        str.append( "://" ).append( getHost( httpServletRequest ) );

        //Appends the port if necessary
        final int port = getPort( httpServletRequest );
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

    public static String getFullUrl( final HttpServletRequest httpServletRequest )
    {
        //Appends the server part
        StringBuffer fullUrl = new StringBuffer( getServerUrl( httpServletRequest ) );

        //Appends the path part
        fullUrl.append( getPath( httpServletRequest ) );

        //Appends the query string part
        final String queryString = getQueryString( httpServletRequest );
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
