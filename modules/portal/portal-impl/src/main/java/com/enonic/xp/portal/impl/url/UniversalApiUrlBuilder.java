package com.enonic.xp.portal.impl.url;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.Multimap;

import com.enonic.xp.portal.impl.ContentResolver;
import com.enonic.xp.portal.impl.ContentResolverResult;
import com.enonic.xp.portal.url.ApiUrlParams;
import com.enonic.xp.site.Site;

import static com.enonic.xp.portal.impl.url.UrlBuilderHelper.appendPathSegments;
import static com.enonic.xp.portal.impl.url.UrlBuilderHelper.appendSubPath;

final class UniversalApiUrlBuilder
    extends PortalUrlBuilder<ApiUrlParams>
{
    private static final Pattern ADMIN_SITE_CTX_PATTERN =
        Pattern.compile( "^(?<mode>edit|preview|admin|inline)/(?<project>[^/]+)/(?<branch>[^/]+)" );

    private static final Pattern SITE_CTX_PATTERN = Pattern.compile( "^(?<project>[^/]+)/(?<branch>[^/]+)" );

    private static final Pattern WEBAPP_CXT_PATTERN = Pattern.compile( "^([^/]+)" );

    private static final Pattern TOOL_CXT_PATTERN = Pattern.compile( "^([^/]+)/([^/]+)" );

    private static final String ADMIN_PREFIX = "/admin";

    private static final String ADMIN_SITE_PREFIX = "/admin/site/";

    private static final String TOOL_PREFIX = "/admin/";

    private static final String SITE_PREFIX = "/site/";

    private static final String WEBAPP_PREFIX = "/webapp/";

    private static final String API_PREFIX = "/api/";

    @Override
    protected void buildUrl( final StringBuilder url, final Multimap<String, String> params )
    {
        url.setLength( 0 );

        final String requestURI = this.portalRequest.getRawRequest().getRequestURI();

        if ( requestURI.equals( ADMIN_PREFIX ) )
        {
            processHome( url );
        }
        else if ( requestURI.startsWith( ADMIN_SITE_PREFIX ) )
        {
            processSite( url, requestURI, true );
        }
        else if ( requestURI.startsWith( TOOL_PREFIX ) )
        {
            processTool( url, requestURI );
        }
        else if ( requestURI.startsWith( SITE_PREFIX ) )
        {
            processSite( url, requestURI, false );
        }
        else if ( requestURI.startsWith( WEBAPP_PREFIX ) )
        {
            processWebapp( url, requestURI );
        }
        else
        {
            appendPart( url, "api" );
        }

        if ( !requestURI.startsWith( API_PREFIX ) )
        {
            appendPart( url, "_" );
        }

        appendPart( url, this.params.getApplication() );
        if ( this.params.getApi() != null )
        {
            appendPart( url, this.params.getApi() );
        }
        appendSubPath( url, this.params.getPath() );
        appendPathSegments( url, this.params.getPathSegments() );

        params.putAll( this.params.getParams() );
    }

    private void processHome( final StringBuilder url )
    {
        appendPart( url, "admin" );
        appendPart( url, "com.enonic.xp.app.main" );
        appendPart( url, "home" );
    }

    private void processSite( final StringBuilder url, final String requestURI, final boolean isSiteAdmin )
    {
        final String sitePrefix = isSiteAdmin ? ADMIN_SITE_PREFIX : SITE_PREFIX;
        final String subPath = subPath( requestURI, sitePrefix );
        final Pattern pattern = isSiteAdmin ? ADMIN_SITE_CTX_PATTERN : SITE_CTX_PATTERN;
        final Matcher matcher = pattern.matcher( subPath );
        if ( matcher.find() )
        {
            if ( isSiteAdmin )
            {
                appendPart( url, "admin" );
            }
            appendPart( url, "site" );
            if ( isSiteAdmin )
            {
                appendPart( url, matcher.group( "mode" ) );
            }
            appendPart( url, matcher.group( "project" ) );
            appendPart( url, matcher.group( "branch" ) );

            final ContentResolverResult contentResolverResult = new ContentResolver( contentService ).resolve( portalRequest );
            final Site site = contentResolverResult.getNearestSite();
            if ( site != null )
            {
                appendPart( url, site.getPath().toString() );
            }
        }
        else
        {
            throw new IllegalArgumentException( String.format( "Invalid site context: %s", subPath ) );
        }
    }

    private void processWebapp( final StringBuilder url, final String requestURI )
    {
        final String subPath = subPath( requestURI, WEBAPP_PREFIX );
        final Matcher matcher = WEBAPP_CXT_PATTERN.matcher( subPath );

        if ( matcher.find() )
        {
            appendPart( url, "webapp" );
            appendPart( url, matcher.group( 0 ) );
        }
        else
        {
            throw new IllegalArgumentException( String.format( "Invalid webapp context: %s", subPath ) );
        }
    }

    private void processTool( final StringBuilder url, final String requestURI )
    {
        final String subPath = subPath( requestURI, TOOL_PREFIX );
        final Matcher matcher = TOOL_CXT_PATTERN.matcher( subPath );
        if ( matcher.find() )
        {
            appendPart( url, "admin" );
            appendPart( url, matcher.group( 0 ) );
        }
        else
        {
            throw new IllegalArgumentException( String.format( "Invalid tool context: %s", subPath ) );
        }
    }

    private String subPath( final String requestURI, final String prefix )
    {
        final int endpoint = requestURI.indexOf( "/_/" );
        final int endIndex = endpoint == -1 ? requestURI.length() : endpoint + 1;
        return requestURI.substring( prefix.length(), endIndex );
    }
}
