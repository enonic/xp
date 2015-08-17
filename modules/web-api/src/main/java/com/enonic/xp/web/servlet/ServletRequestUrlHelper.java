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

    public static String createServerUrl()
    {
        return createServerUrl( ServletRequestHolder.getRequest() );
    }

    private static String createServerUrl( final HttpServletRequest httpServletRequest )
    {
        final StringBuilder str = new StringBuilder();

        //Appends the scheme part
        String scheme = getScheme( httpServletRequest );
        str.append( scheme );

        //Appends the host
        str.append( "://" ).append( getHost( httpServletRequest ) );

        //Appends the port if necessary
        final String port = getPort( httpServletRequest );
        if ( needPortNumber( scheme, port ) )
        {
            str.append( ":" ).append( port );
        }

        return str.toString();
    }

    public static String getScheme()
    {
        return getScheme( ServletRequestHolder.getRequest() );
    }

    private static String getScheme( final HttpServletRequest httpServletRequest )
    {
        String scheme = httpServletRequest.getHeader( X_FORWARDED_PROTO );
        if ( scheme == null )
        {
            scheme = httpServletRequest.getScheme();
        }
        return scheme;
    }

    public static String getHost()
    {
        return getHost( ServletRequestHolder.getRequest() );
    }

    private static String getHost( final HttpServletRequest httpServletRequest )
    {
        String xForwardedHost = httpServletRequest.getHeader( X_FORWARDED_HOST );
        if ( xForwardedHost != null )
        {
            return xForwardedHost.split( ":" )[0];
        }
        else
        {
            return httpServletRequest.getServerName();
        }
    }

    public static String getPort()
    {
        return getPort( ServletRequestHolder.getRequest() );
    }

    private static String getPort( final HttpServletRequest httpServletRequest )
    {
        String xForwardedHost = httpServletRequest.getHeader( X_FORWARDED_HOST );
        if ( xForwardedHost != null )
        {
            final String[] xForwardedHostValues = xForwardedHost.split( ":" );
            if ( xForwardedHostValues.length > 1 )
            {
                return xForwardedHostValues[1];
            }
        }
        return Integer.toString( httpServletRequest.getServerPort() );
    }

    private static boolean needPortNumber( final String scheme, final String port )
    {
        final boolean isHttp = "http".equals( scheme ) && ( "80".equals( port ) );
        final boolean isHttps = "https".equals( scheme ) && ( "443".equals( port ) );
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
        else
        {
            throw new OutOfScopeException( "URI out of scope" );
        }
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
