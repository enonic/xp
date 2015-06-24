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
        final StringBuilder str = new StringBuilder();

        str.append( createHost( req ) );
        str.append( createUri( path ) );

        return str.toString();
    }

    private static String createHost( final HttpServletRequest req )
    {
        final StringBuilder str = new StringBuilder();

        final String scheme = req.getScheme();
        final int port = req.getServerPort();

        str.append( scheme ).append( "://" );
        str.append( req.getServerName() );

        if ( needPortNumber( scheme, port ) )
        {
            str.append( ":" ).append( port );
        }
        return str.toString();
    }

    public static String createBaseUrl( final String baseUri, final String branch, final String contentPath )
    {
        return createBaseUrl( ServletRequestHolder.getRequest(), baseUri, branch, contentPath );
    }

    public static String createBaseUrl( final HttpServletRequest req, final String baseUri, final String branch, final String contentPath )
    {
        final String host = createHost( req );
        final String normalizedBaseUri = normalizePath( baseUri );
        final String normalizedBranch = normalizePath( branch );
        final String normalizedContentPath = normalizePath( contentPath );
        final String baseUriAndBranch = normalizedBaseUri + normalizedBranch;
        final String uri = normalizedBaseUri + normalizedBranch + normalizedContentPath;

        //The base URL starts with the host
        final StringBuilder baseUrl = new StringBuilder( host );

        //If the URI is rewritten
        final UriRewritingResult rewritingResult = rewriteUri( req, uri );
        if ( rewritingResult.getNewUriPrefix() != null )
        {
            //Appends the rewritten part to the host
            baseUrl.append( rewritingResult.getNewUriPrefix() );

            if ( rewritingResult.getDeletedUriPrefix().startsWith( normalizedBaseUri ) )
            {
                if ( !rewritingResult.getDeletedUriPrefix().startsWith( baseUriAndBranch ) )
                {
                    //If the baseUri has been rewritten but not the branch, append the branch to the rewritten part
                    baseUrl.append( normalizedBranch );
                }
            }
            else
            {
                // If the baseUri has not been rewritten, appends the baseUri and the branch to the rewritten part
                baseUrl.append( baseUriAndBranch );
            }
        }
        else
        {
            //If there is no rewriting, appends the baseUri and the branch to the host
            baseUrl.append( baseUriAndBranch );
        }

        return baseUrl.toString();
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
