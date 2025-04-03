package com.enonic.xp.portal.impl.url;

import java.math.BigInteger;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.exception.NotFoundException;
import com.enonic.xp.portal.impl.exception.OutOfScopeException;

import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.base.Strings.nullToEmpty;

final class UrlGenerator
{
    private static final Logger LOG = LoggerFactory.getLogger( UrlGenerator.class );

    public static String generateUrl( final UrlGeneratorParams params )
    {
        try
        {
            final String baseUrl = removeTrailingSlash( params.getBaseUrl() != null ? params.getBaseUrl().get() : null );
            final String path = normalizePath( params.getPath() != null ? params.getPath().get() : null );
            final String queryParams =
                nullToEmpty( params.getQueryString() != null ? params.getQueryString().get() : null );

            return baseUrl + path + queryParams;
        }
        catch ( Exception e )
        {
            return buildErrorUrl( e );
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

        return !path.startsWith( "/" ) ? "/" + path : path;
    }

    private static String buildErrorUrl( final Exception e )
    {
        final String logRef = LOG.isWarnEnabled() ? newLogRef() : "";
        LOG.warn( "Portal url build failed. Logref: {}", logRef, e );

        if ( e instanceof NotFoundException )
        {
            return buildErrorUrl( 404, String.join( " ", "Not Found.", logRef ) );
        }
        else if ( e instanceof OutOfScopeException )
        {
            return buildErrorUrl( 400, String.join( " ", "Out of scope.", logRef ) );
        }
        else
        {
            return buildErrorUrl( 500, String.join( " ", "Something went wrong.", logRef ) );
        }
    }

    private static String newLogRef()
    {
        return new BigInteger( UUID.randomUUID().toString().replace( "-", "" ), 16 ).toString( 32 );
    }

    private static String buildErrorUrl( final int code, final String message )
    {
        final StringBuilder result = new StringBuilder();

        UrlBuilderHelper.appendPart( result, "_" );
        UrlBuilderHelper.appendPart( result, "error" );
        UrlBuilderHelper.appendPart( result, String.valueOf( code ) );
        UrlBuilderHelper.appendParams( result, Map.of( "message", message ).entrySet() );

        return result.toString();
    }
}
