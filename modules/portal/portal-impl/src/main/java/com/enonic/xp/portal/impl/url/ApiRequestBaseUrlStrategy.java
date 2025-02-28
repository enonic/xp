package com.enonic.xp.portal.impl.url;

import java.util.Objects;

import com.enonic.xp.content.ContentService;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.impl.ContentResolver;
import com.enonic.xp.portal.impl.ContentResolverResult;
import com.enonic.xp.portal.url.BaseUrlStrategy;
import com.enonic.xp.portal.url.UrlTypeConstants;
import com.enonic.xp.site.Site;

import static com.enonic.xp.portal.impl.url.UrlBuilderHelper.appendPart;
import static com.enonic.xp.portal.impl.url.UrlBuilderHelper.rewriteUri;

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
            appendPart( url, "admin" );
            appendPart( url, "com.enonic.xp.app.main" );
            appendPart( url, "home" );
        }
        else if ( portalRequest.isSiteBase() )
        {
            appendPart( url, baseUri );
            appendPart( url, portalRequest.getRepositoryId().toString() );
            appendPart( url, portalRequest.getBranch().toString() );
            final ContentResolverResult contentResolverResult = new ContentResolver( contentService ).resolve( portalRequest );
            final Site site = contentResolverResult.getNearestSite();
            if ( site != null )
            {
                appendPart( url, site.getPath().toString() );
            }
        }
        else if ( baseUri.startsWith( "/admin/" ) || baseUri.startsWith( "/webapp/" ) )
        {
            appendPart( url, baseUri );
        }
        else
        {
            appendPart( url, "/api" );
            isSlashApi = true;
        }

        if ( !isSlashApi )
        {
            appendPart( url, "_" );
        }

        final String baseUrl = url.toString();

        return rewriteUri( portalRequest.getRawRequest(), urlType, baseUrl );
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
