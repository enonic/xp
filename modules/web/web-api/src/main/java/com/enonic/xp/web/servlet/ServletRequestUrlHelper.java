package com.enonic.xp.web.servlet;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

import com.google.common.base.Splitter;

import jakarta.servlet.http.HttpServletRequest;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.web.vhost.VirtualHost;
import com.enonic.xp.web.vhost.VirtualHostHelper;

@PublicApi
public final class ServletRequestUrlHelper
{
    private static final int[] RFC_8187_ATTR_CHAR =
        "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!#$&+-.^_`|~".chars().sorted().toArray();

    public static String createUri( final HttpServletRequest req, final String path )
    {
        return Objects.requireNonNullElse( rewriteUri( VirtualHostHelper.getVirtualHost( req ), path ), path );
    }

    public static String getServerUrl( final HttpServletRequest req )
    {
        return getOrigin( req ).toString();
    }

    private static StringBuilder getOrigin( final HttpServletRequest req )
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

        return str;
    }

    public static String getFullUrl( final HttpServletRequest req )
    {
        final StringBuilder fullUrl = getOrigin( req );
        fullUrl.append( createUri( req, req.getRequestURI() ) );

        final String queryString = req.getQueryString();
        if ( queryString != null )
        {
            fullUrl.append( "?" ).append( queryString );
        }

        return fullUrl.toString();
    }

    private static boolean needPortNumber( final String scheme, final int port )
    {
        return switch ( port )
        {
            case 80 -> !"http".equals( scheme ) && !"ws".equals( scheme );
            case 443 -> !"https".equals( scheme ) && !"wss".equals( scheme );
            default -> port > 0;
        };
    }

    public static UriRewritingResult rewriteUri( final HttpServletRequest req, final String uri )
    {
        final UriRewritingResult.Builder resultBuilder = UriRewritingResult.create();

        final VirtualHost vhost = VirtualHostHelper.getVirtualHost( req );
        if ( vhost == null )
        {
            return resultBuilder.rewrittenUri( uri ).build();
        }

        final String rewrittenUri = rewriteUri( vhost, uri );

        final String source = vhost.getSource();
        final String target = vhost.getTarget();

        return resultBuilder.deletedUriPrefix( target )
            .newUriPrefix( source )
            .rewrittenUri( Objects.requireNonNullElse( rewrittenUri, uri ) )
            .outOfScope( rewrittenUri == null )
            .build();
    }

    private static String rewriteUri( final VirtualHost vhost, final String uri )
    {
        if ( vhost == null || !uri.startsWith( "/" ) )
        {
            return uri;
        }

        final String source = vhost.getSource();
        final String target = vhost.getTarget();

        if ( target.equals( "/" ) )
        {
            return normalizePath( "/".equals( source ) ? uri : source + uri );
        }

        final int queryPos = uri.indexOf( '?' );
        final int pathLength = queryPos == -1 ? uri.length() : queryPos;

        final int targetLength = target.length();

        if ( uri.startsWith( target ) &&
            ( pathLength == targetLength || ( pathLength > targetLength && uri.charAt( targetLength ) == '/' ) ) )
        {
            final StringBuilder sb = new StringBuilder();
            if ( !"/".equals( source ) )
            {
                sb.append( source );
            }
            sb.append( uri, targetLength, uri.length() );
            return normalizePath( sb.toString() );
        }

        return null;
    }

    private static String normalizePath( final String path )
    {
        return Splitter.on( '/' ).omitEmptyStrings().trimResults().splitToStream( path ).collect( Collectors.joining( "/", "/", "" ) );
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
        builder.append( '"' );
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
        builder.append( '"' );
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

    private ServletRequestUrlHelper()
    {
    }
}
