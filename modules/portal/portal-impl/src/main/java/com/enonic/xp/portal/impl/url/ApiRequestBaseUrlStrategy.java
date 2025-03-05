package com.enonic.xp.portal.impl.url;

import java.util.Objects;

import com.enonic.xp.content.ContentService;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.impl.ContentResolver;
import com.enonic.xp.portal.impl.ContentResolverResult;
import com.enonic.xp.portal.url.BaseUrlStrategy;
import com.enonic.xp.portal.url.UrlTypeConstants;
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.site.Site;

final class ApiRequestBaseUrlStrategy
    implements BaseUrlStrategy
{
    private final ContentService contentService;

    private final PortalRequest portalRequest;

    private final String urlType;

    private ApiRequestBaseUrlStrategy( final Builder builder )
    {
        this.contentService = Objects.requireNonNull( builder.contentService );
        this.portalRequest = Objects.requireNonNull( builder.portalRequest );
        this.urlType = Objects.requireNonNullElse( builder.urlType, UrlTypeConstants.SERVER_RELATIVE );
    }

    @Override
    public String generateBaseUrl()
    {
        final StringBuilder url = new StringBuilder();

        final String baseUri = portalRequest.getBaseUri();

        boolean isSlashApi = false;

        if ( baseUri.equals( "/admin" ) )
        {
            url.append( "/admin/com.enonic.xp.app.main/home" );
        }
        else if ( portalRequest.isSiteBase() )
        {
            url.append( baseUri )
                .append( "/" )
                .append( ProjectName.from( portalRequest.getRepositoryId() ) )
                .append( "/" )
                .append( portalRequest.getBranch() );

            final ContentResolverResult contentResolverResult = new ContentResolver( contentService ).resolve( portalRequest );
            final Site site = contentResolverResult.getNearestSite();
            if ( site != null )
            {
                UrlBuilderHelper.appendAndEncodePathParts( url, site.getPath().toString() );
            }
        }
        else if ( baseUri.startsWith( "/admin/" ) || baseUri.startsWith( "/webapp/" ) )
        {
            url.append( baseUri );
        }
        else
        {
            url.append( "/api" );
            isSlashApi = true;
        }

        if ( !isSlashApi )
        {
            UrlBuilderHelper.appendPart( url, "_" );
        }

        final String baseUrl = url.toString();

        return UrlBuilderHelper.rewriteUri( portalRequest.getRawRequest(), urlType, baseUrl );
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

        public ApiRequestBaseUrlStrategy build()
        {
            return new ApiRequestBaseUrlStrategy( this );
        }
    }
}
