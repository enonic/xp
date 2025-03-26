package com.enonic.xp.portal.impl.url;

import java.util.Objects;

import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.impl.ContentResolver;
import com.enonic.xp.portal.url.BaseUrlStrategy;
import com.enonic.xp.portal.url.UrlTypeConstants;
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.site.Site;

final class SiteRequestBaseUrlStrategy
    implements BaseUrlStrategy
{
    private final ContentService contentService;

    private final PortalRequest portalRequest;

    private final String urlType;

    private SiteRequestBaseUrlStrategy( final Builder builder )
    {
        this.contentService = Objects.requireNonNull( builder.contentService );
        this.portalRequest = Objects.requireNonNull( builder.portalRequest );
        this.urlType = Objects.requireNonNullElse( builder.urlType, UrlTypeConstants.SERVER_RELATIVE );
    }

    @Override
    public String generateBaseUrl()
    {
        return UrlBuilderHelper.rewriteUri( portalRequest.getRawRequest(), urlType, generateUri() );
    }

    private String generateUri()
    {
        final StringBuilder uriBuilder = new StringBuilder( portalRequest.getBaseUri() );

        if ( portalRequest.isSiteBase() )
        {
            UrlBuilderHelper.appendSubPath( uriBuilder, ProjectName.from( portalRequest.getRepositoryId() ).toString() );
            UrlBuilderHelper.appendSubPath( uriBuilder, portalRequest.getBranch().getValue() );
            UrlBuilderHelper.appendAndEncodePathParts( uriBuilder, resolveSitePath().toString() );
        }

        UrlBuilderHelper.appendPart( uriBuilder, "_" );

        return uriBuilder.toString();
    }

    private ContentPath resolveSitePath()
    {
        final Context context = ContextBuilder.copyOf( ContextAccessor.current() )
            .repositoryId( portalRequest.getRepositoryId() )
            .branch( portalRequest.getBranch() )
            .build();

        Site nearestSite = context.callWith( () -> {
            final ContentResolver contentResolver = new ContentResolver( contentService );
            return contentResolver.resolve( portalRequest ).getNearestSite();
        } );

        return nearestSite != null ? nearestSite.getPath() : ContentPath.ROOT;
    }

    public static Builder create()
    {
        return new Builder();
    }

    static class Builder
    {
        private ContentService contentService;

        private PortalRequest portalRequest;

        private String urlType;

        public Builder setContentService( final ContentService contentService )
        {
            this.contentService = contentService;
            return this;
        }

        public Builder setPortalRequest( final PortalRequest portalRequest )
        {
            this.portalRequest = portalRequest;
            return this;
        }

        public Builder setUrlType( final String urlType )
        {
            this.urlType = urlType;
            return this;
        }

        public SiteRequestBaseUrlStrategy build()
        {
            return new SiteRequestBaseUrlStrategy( this );
        }
    }
}
