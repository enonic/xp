package com.enonic.xp.portal.impl.url;

import java.util.Objects;

import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.url.BaseUrlStrategy;
import com.enonic.xp.portal.url.UrlTypeConstants;
import com.enonic.xp.project.ProjectName;

final class PageRequestBaseUrlStrategy
    implements BaseUrlStrategy
{
    private final PortalRequest portalRequest;

    private final String urlType;

    private PageRequestBaseUrlStrategy( final Builder builder )
    {
        this.portalRequest = Objects.requireNonNull( builder.portalRequest );
        this.urlType = Objects.requireNonNullElse( builder.urlType, UrlTypeConstants.SERVER_RELATIVE );
    }

    @Override
    public String generateBaseUrl()
    {
        final StringBuilder uriBuilder = new StringBuilder( portalRequest.getBaseUri() );

        if ( portalRequest.isSiteBase() )
        {
            UrlBuilderHelper.appendSubPath( uriBuilder, ProjectName.from( portalRequest.getRepositoryId() ).toString() );
            UrlBuilderHelper.appendSubPath( uriBuilder, portalRequest.getBranch().getValue() );
        }

        return UrlBuilderHelper.rewriteUri( portalRequest.getRawRequest(), urlType, uriBuilder.toString() );
    }

    public static Builder create()
    {
        return new Builder();
    }

    static class Builder
    {
        private String urlType;

        private PortalRequest portalRequest;

        public Builder setUrlType( final String urlType )
        {
            this.urlType = urlType;
            return this;
        }

        public Builder setPortalRequest( final PortalRequest portalRequest )
        {
            this.portalRequest = portalRequest;
            return this;
        }

        public PageRequestBaseUrlStrategy build()
        {
            return new PageRequestBaseUrlStrategy( this );
        }
    }
}
