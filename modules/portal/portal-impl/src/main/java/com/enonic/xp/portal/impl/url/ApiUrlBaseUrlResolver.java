package com.enonic.xp.portal.impl.url;

import java.util.Objects;
import java.util.function.Supplier;

import com.enonic.xp.content.ContentService;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalRequestAccessor;
import com.enonic.xp.portal.impl.ContentResolver;
import com.enonic.xp.portal.url.ApiUrlGeneratorParams;
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.site.Site;

final class ApiUrlBaseUrlResolver
    implements Supplier<String>
{
    private final ContentService contentService;

    private final ApiUrlGeneratorParams params;

    ApiUrlBaseUrlResolver( final ContentService contentService, final ApiUrlGeneratorParams params )
    {
        this.contentService = contentService;
        this.params = params;
    }

    @Override
    public String get()
    {
        final PortalRequest portalRequest = PortalRequestAccessor.get();

        final StringBuilder url = new StringBuilder( generateBaseUrlPrefix( portalRequest ) );

        final String application = Objects.requireNonNull( params.getApplication().get(), "Application must be set" );

        UrlBuilderHelper.appendPart( url, application + ":" + params.getApi() );

        if ( params.getBaseUrl() != null )
        {
            return url.toString();
        }
        else if ( portalRequest != null && !portalRequest.getBaseUri().isEmpty() )
        {
            return UrlBuilderHelper.rewriteUri( portalRequest.getRawRequest(), params.getUrlType(), url.toString() );
        }
        else
        {
            return url.toString();
        }
    }

    private String generateBaseUrlPrefix( final PortalRequest portalRequest )
    {
        final StringBuilder url = new StringBuilder();

        if ( params.getBaseUrl() != null )
        {
            url.append( params.getBaseUrl() );
            UrlBuilderHelper.appendPart( url, "_" );
        }
        else if ( portalRequest == null || portalRequest.getBaseUri().isEmpty() || portalRequest.getBaseUri().startsWith( "/api/" ) )
        {
            url.append( "/api" );
        }
        else if ( portalRequest.isSiteBase() )
        {
            url.append( portalRequest.getBaseUri() );
            UrlBuilderHelper.appendSubPath( url, ProjectName.from( portalRequest.getRepositoryId() ).toString() );
            UrlBuilderHelper.appendSubPath( url, portalRequest.getBranch().getValue() );

            final Context context = ContextBuilder.copyOf( ContextAccessor.current() )
                .repositoryId( portalRequest.getRepositoryId() )
                .branch( portalRequest.getBranch() )
                .build();

            final Site nearestSite = context.callWith( () -> {
                final ContentResolver contentResolver = new ContentResolver( contentService );
                return contentResolver.resolve( portalRequest ).getNearestSite();
            } );

            if ( nearestSite != null )
            {
                UrlBuilderHelper.appendAndEncodePathParts( url, nearestSite.getPath().toString() );
            }

            UrlBuilderHelper.appendPart( url, "_" );
        }
        else if ( portalRequest.getBaseUri().equals( "/admin" ) )
        {
            url.append( "/admin/com.enonic.xp.app.main/home/_/" );
        }
        else
        {
            url.append( portalRequest.getBaseUri() );
            UrlBuilderHelper.appendPart( url, "_" );
        }

        return url.toString();
    }
}
