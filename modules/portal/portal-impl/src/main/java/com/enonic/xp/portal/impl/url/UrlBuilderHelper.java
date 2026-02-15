package com.enonic.xp.portal.impl.url;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.google.common.base.Splitter;
import com.google.common.net.UrlEscapers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import com.enonic.xp.portal.impl.exception.OutOfScopeException;
import com.enonic.xp.portal.url.UrlTypeConstants;
import com.enonic.xp.web.servlet.ServletRequestUrlHelper;
import com.enonic.xp.web.servlet.UriRewritingResult;

import static com.google.common.base.Strings.isNullOrEmpty;

public final class UrlBuilderHelper
{
    private UrlBuilderHelper()
    {
    }

    public static void appendPart( final StringBuilder str, final String urlPart )
    {
        if ( isNullOrEmpty( urlPart ) )
        {
            return;
        }

        final boolean endsWithSlash = !str.isEmpty() && str.charAt( str.length() - 1 ) == '/';
        final String normalized = urlEncodePathSegment( urlPart );

        if ( !endsWithSlash )
        {
            str.append( "/" );
        }

        str.append( normalized );
    }

    public static void appendPartWithoutEncode( final StringBuilder str, final String urlPart )
    {
        if ( isNullOrEmpty( urlPart ) )
        {
            return;
        }

        final boolean endsWithSlash = !str.isEmpty() && str.charAt( str.length() - 1 ) == '/';

        if ( !endsWithSlash && !urlPart.startsWith( "/" ) )
        {
            str.append( "/" );
        }

        str.append( urlPart );
    }

    public static void appendPathSegments( final StringBuilder url, final Collection<String> pathSegments )
    {
        if ( pathSegments != null )
        {
            pathSegments.forEach( pathSegment -> {
                if ( !isNullOrEmpty( pathSegment ) )
                {
                    appendPart( url, pathSegment );
                }
            } );
        }
    }

    public static void appendSubPath( final StringBuilder url, final String subPath )
    {
        appendPartWithoutEncode( url, subPath );
    }

    public static void appendAndEncodePathParts( final StringBuilder str, final String value )
    {
        if ( isNullOrEmpty( value ) )
        {
            return;
        }

        final boolean endsWithSlash = !str.isEmpty() && str.charAt( str.length() - 1 ) == '/';
        if ( !endsWithSlash )
        {
            str.append( "/" );
        }

        if ( !value.contains( "/" ) )
        {
            str.append( urlEncodePathSegment( value ) );
        }
        else
        {
            str.append( StreamSupport.stream( Splitter.on( '/' ).trimResults().omitEmptyStrings().split( value ).spliterator(), false )
                            .map( UrlBuilderHelper::urlEncodePathSegment )
                            .collect( Collectors.joining( "/" ) ) );
        }
    }

    private static String urlEncodePathSegment( final String value )
    {
        return UrlEscapers.urlPathSegmentEscaper().escape( value );
    }

    public static void appendParams( final StringBuilder str, final Map<String, List<String>> params )
    {
        if ( params.isEmpty() )
        {
            return;
        }

        str.append( "?" );
        final Iterator<Map.Entry<String, List<String>>> it = params.entrySet().iterator();
        appendParam( str, it.next() );
        while ( it.hasNext() )
        {
            str.append( "&" );
            appendParam( str, it.next() );
        }
    }

    private static void appendParam( final StringBuilder str, final Map.Entry<String, List<String>> param )
    {
        final List<String> values = param.getValue();
        final String encodedKey = urlEncode( param.getKey() );

        if ( values.isEmpty() )
        {
            str.append( encodedKey );
        }
        else
        {
            final Iterator<String> it = values.iterator();
            str.append( encodedKey ).append( "=" ).append( urlEncode( it.next() ) );
            while ( it.hasNext() )
            {
                str.append( "&" ).append( encodedKey ).append( "=" ).append( urlEncode( it.next() ) );
            }
        }
    }

    public static String urlEncode( final String value )
    {
        return UrlEscapers.urlFormParameterEscaper().escape( value );
    }

    public static String rewriteUri( final HttpServletRequest request, final String urlType, final String uri )
    {
        final UriRewritingResult rewritingResult = ServletRequestUrlHelper.rewriteUri( request, uri );

        if ( rewritingResult.isOutOfScope() )
        {
            throw new OutOfScopeException( "URI out of scope" );
        }

        final String rewrittenUri = rewritingResult.getRewrittenUri();

        return buildServerUrl( request, urlType ) + rewrittenUri;
    }

    public static String buildServerUrl( final HttpServletRequest request, final String urlType )
    {
        if ( UrlTypeConstants.ABSOLUTE.equals( urlType ) )
        {
            return ServletRequestUrlHelper.getServerUrl( request );
        }
        else if ( UrlTypeConstants.WEBSOCKET.equals( urlType ) )
        {
            return ServletRequestUrlHelper.getServerUrl( new HttpServletRequestWrapper( request )
            {
                @Override
                public String getScheme()
                {
                    return isSecure() ? "wss" : "ws";
                }
            } );
        }
        else
        {
            return "";
        }
    }
}
