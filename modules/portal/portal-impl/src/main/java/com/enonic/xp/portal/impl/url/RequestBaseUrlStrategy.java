package com.enonic.xp.portal.impl.url;

import java.util.Objects;

import com.enonic.xp.content.ContentService;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.impl.ContentResolver;
import com.enonic.xp.portal.url.BaseUrlStrategy;
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.site.Site;

final class RequestBaseUrlStrategy
    implements BaseUrlStrategy
{
    private final PortalRequest portalRequest;

    private final ContentService contentService;

    RequestBaseUrlStrategy( final PortalRequest portalRequest, final ContentService contentService )
    {
        this.portalRequest = Objects.requireNonNull( portalRequest );
        this.contentService = contentService;
    }

    @Override
    public String generateBaseUrl()
    {
        if ( portalRequest.isSiteBase() )
        {
            final StringBuilder url = new StringBuilder( portalRequest.getBaseUri() );
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

            return url.toString();
        }
        else if ( portalRequest.getBaseUri().equals( "/admin" ) )
        {
            return "/admin/com.enonic.xp.app.main/home/_/";
        }
        else
        {
            return portalRequest.getBaseUri() + "/_/";
        }
    }
}
