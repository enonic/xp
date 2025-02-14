package com.enonic.xp.portal.impl.url;

import java.math.BigInteger;
import java.util.Objects;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.exception.NotFoundException;
import com.enonic.xp.portal.impl.exception.OutOfScopeException;
import com.enonic.xp.portal.url.BaseUrlStrategy;

import static com.enonic.xp.portal.impl.url.UrlBuilderHelper.urlEncode;
import static com.google.common.base.Strings.isNullOrEmpty;

final class UrlGenerator
{
    private static final Logger LOG = LoggerFactory.getLogger( UrlGenerator.class );

    public static String generateUrl( final BaseUrlStrategy baseUrlStrategy, final PathStrategy pathStrategy )
    {
        String baseUrl = null;
        try
        {
            baseUrl = removeTrailingSlash( baseUrlStrategy.generateBaseUrl() );
            final String path = normalizePath( pathStrategy.generatePath() );
            return baseUrl + path;
        }
        catch ( Exception e )
        {
            return buildErrorUrl( baseUrl, e );
        }
    }

    private static String removeTrailingSlash( final String path )
    {
        if ( isNullOrEmpty( path ) )
        {
            return "";
        }

        return path.endsWith( "/" ) ? path.substring( 0, path.length() - 1 ) : path;
    }

    private static String normalizePath( final String path )
    {
        if ( isNullOrEmpty( path ) )
        {
            return "";
        }

        if ( path.startsWith( "?" ) )
        {
            return path;
        }

        return !path.startsWith( "/" ) ? "/" + path : path;
    }

    private static String buildErrorUrl( final String baseUrl, final Exception e )
    {
        final String logRef = LOG.isWarnEnabled() ? newLogRef() : "";
        LOG.warn( "Portal url build failed. Logref: {}", logRef, e );

        if ( e instanceof NotFoundException )
        {
            return buildErrorUrl( baseUrl, 404, String.join( " ", "Not Found.", logRef ) );
        }
        else if ( e instanceof OutOfScopeException )
        {
            return buildErrorUrl( baseUrl, 400, String.join( " ", "Out of scope.", logRef ) );
        }
        else
        {
            return buildErrorUrl( baseUrl, 500, String.join( " ", "Something went wrong.", logRef ) );
        }
    }

    private static String newLogRef()
    {
        return new BigInteger( UUID.randomUUID().toString().replace( "-", "" ), 16 ).toString( 32 );
    }

    private static String buildErrorUrl( final String baseUrl, final int code, final String message )
    {
        return Objects.requireNonNullElse( baseUrl, "/_" ) + "/error/" + code + "?message=" + urlEncode( message );
    }
}
