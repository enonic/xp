package com.enonic.xp.web.servlet;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import com.google.common.base.Splitter;
import com.google.common.net.HostAndPort;

import jakarta.servlet.http.HttpServletRequest;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.web.vhost.VirtualHost;
import com.enonic.xp.web.vhost.VirtualHostHelper;

import static com.google.common.base.Strings.isNullOrEmpty;

@PublicApi
public final class ServletRequestUrlHelper
{
    private static final int[] RFC_8187_ATTR_CHAR =
        "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!#$&+-.^_`|~".chars().sorted().toArray();

    @Deprecated
    public static String createUri( final String path )
    {
        return createUri( ServletRequestHolder.getRequest(), path );
    }

    public static String createUri( final HttpServletRequest req, final String path )
    {
        return rewriteUri( req, path.startsWith( "/" ) ? path : "/" + path ).getRewrittenUri();
    }

    @Deprecated
    public static String getScheme()
    {
        return getScheme( ServletRequestHolder.getRequest() );
    }

    @Deprecated
    public static String getScheme( final HttpServletRequest req )
    {
        return req.getScheme();
    }

    @Deprecated
    public static String getHost()
    {
        return getHost( ServletRequestHolder.getRequest() );
    }

    @Deprecated
    public static String getHost( final HttpServletRequest req )
    {
        return req.getServerName();
    }

    @Deprecated
    public static int getPort()
    {
        return getPort( ServletRequestHolder.getRequest() );
    }

    @Deprecated
    public static int getPort( final HttpServletRequest req )
    {
        return req.getServerPort();
    }

    @Deprecated
    public static HostAndPort getHostAndPort()
    {
        return getHostAndPort( ServletRequestHolder.getRequest() );
    }

    @Deprecated
    public static HostAndPort getHostAndPort( final HttpServletRequest req )
    {
        return HostAndPort.fromParts( req.getServerName(), req.getServerPort() );
    }

    @Deprecated
    public static String getRemoteAddress( final HttpServletRequest req )
    {
        return req.getRemoteAddr();
    }

    @Deprecated
    public static String getPath()
    {
        return getPath( ServletRequestHolder.getRequest() );
    }

    public static String getPath( final HttpServletRequest req )
    {
        return rewriteUri( req, req.getRequestURI() ).getRewrittenUri();
    }

    @Deprecated
    private static String getQueryString( final HttpServletRequest req )
    {
        return req.getQueryString();
    }

    @Deprecated
    public static String getServerUrl()
    {
        return getServerUrl( ServletRequestHolder.getRequest() );
    }

    public static String getServerUrl( final HttpServletRequest req )
    {
        final StringBuilder str = new StringBuilder();

        //Appends the scheme part
        String scheme = req.getScheme();
        str.append( scheme );

        //Appends the host
        str.append( "://" ).append( req.getServerName() );

        //Appends the port if necessary
        final int port = req.getServerPort();
        if ( needPortNumber( scheme, port ) )
        {
            str.append( ":" ).append( port );
        }

        return str.toString();
    }

    @Deprecated
    public static String getFullUrl()
    {
        return getFullUrl( ServletRequestHolder.getRequest() );
    }

    public static String getFullUrl( final HttpServletRequest req )
    {
        //Appends the server part
        StringBuilder fullUrl = new StringBuilder( getServerUrl( req ) );

        //Appends the path part
        fullUrl.append( rewriteUri( req, req.getRequestURI() ).getRewrittenUri() );

        //Appends the query string part
        final String queryString = req.getQueryString();
        if ( queryString != null )
        {
            fullUrl.append( "?" ).append( queryString );
        }

        return fullUrl.toString();
    }

    private static boolean needPortNumber( final String scheme, final int port )
    {
        final boolean isUndefined = port < 0;
        final boolean isHttpOrWs = ( "http".equals( scheme ) || "ws".equals( scheme ) ) && 80 == port;
        final boolean isHttpsOrWss = ( "https".equals( scheme ) || "wss".equals( scheme ) ) && 443 == port;
        return !( isUndefined || isHttpOrWs || isHttpsOrWss );
    }

    @Deprecated
    public static UriRewritingResult rewriteUri( final String uri )
    {
        return rewriteUri( ServletRequestHolder.getRequest(), uri );
    }

    public static UriRewritingResult rewriteUri( final HttpServletRequest req, final String uri )
    {
        UriRewritingResult.Builder resultBuilder = UriRewritingResult.create();
        final VirtualHost vhost = VirtualHostHelper.getVirtualHost( req );
        if ( vhost == null )
        {
            return resultBuilder.rewrittenUri( uri ).build();
        }

        final String targetPath = vhost.getTarget();
        if ( needRewrite( uri, targetPath ) )
        {
            final String result = uri.substring( targetPath.length() );
            final String newUri = normalizePath( vhost.getSource() + ( "/".equals( targetPath ) ? "/" : "" ) + result );
            return resultBuilder.rewrittenUri( newUri )
                .deletedUriPrefix( targetPath )
                .newUriPrefix( normalizePath( vhost.getSource() ) )
                .build();
        }

        return resultBuilder.rewrittenUri( normalizePath( uri ) ).outOfScope( true ).build();
    }

    private static boolean needRewrite( final String uri, final String targetPath )
    {
        if ( targetPath.equals( "/" ) )
        {
            return uri.startsWith( "/" );
        }
        if ( uri.equals( targetPath ) )
        {
            return true;
        }

        final int queryPos = uri.indexOf( '?' );

        if ( queryPos == -1 )
        {
            return uri.startsWith( targetPath + "/" );
        }
        else
        {
            final String uriWithoutQuery = uri.substring( 0, queryPos );
            if ( uriWithoutQuery.equals( targetPath ) )
            {
                return true;
            }
            else
            {
                return uriWithoutQuery.startsWith( targetPath + "/" );
            }
        }
    }

    private static String normalizePath( final String value )
    {
        if ( isNullOrEmpty( value ) )
        {
            return "/";
        }

        final Iterable<String> parts = Splitter.on( '/' ).trimResults().omitEmptyStrings().split( value );
        return "/" + String.join( "/", parts );
    }

    public static String contentDispositionAttachment( final String fileName )
    {
        final StringBuilder builder = new StringBuilder();
        builder.append( "attachment; filename=" );
        appendQuoted( builder, fileName );
        builder.append( "; filename*=" );
        appendRfc8187Encoded( builder, fileName, StandardCharsets.UTF_8 );
        return builder.toString();
    }

    private static void appendQuoted( final StringBuilder builder, final String input )
    {
        builder.append( "\"" );
        input.codePoints().forEachOrdered( value -> {
            if ( value == '"' )
            {
                builder.append( "\\\"" );
            }
            else
            {
                builder.appendCodePoint( value );
            }
        } );
        builder.append( "\"" );
    }

    private static void appendRfc8187Encoded( final StringBuilder builder, final String input, final Charset charset )
    {
        builder.append( charset.name() );
        builder.append( "''" );
        for ( byte b : input.getBytes( charset ) )
        {
            if ( Arrays.binarySearch( RFC_8187_ATTR_CHAR, b ) >= 0 )
            {
                builder.append( (char) b );
            }
            else
            {
                builder.append( '%' );
                builder.append( Character.toUpperCase( Character.forDigit( ( b >>> 4 ) & 0x0f, 16 ) ) );
                builder.append( Character.toUpperCase( Character.forDigit( b & 0x0f, 16 ) ) );
            }
        }
    }
}
