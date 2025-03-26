package com.enonic.xp.portal.impl.url;

import java.util.Objects;

import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.url.BaseUrlStrategy;
import com.enonic.xp.portal.url.UrlTypeConstants;

final class NonSiteRequestBaseUrlStrategy
    implements BaseUrlStrategy
{
    private final PortalRequest portalRequest;

    private final String urlType;

    private NonSiteRequestBaseUrlStrategy( final Builder builder )
    {
        this.portalRequest = Objects.requireNonNull( builder.portalRequest );
        this.urlType = Objects.requireNonNullElse( builder.urlType, UrlTypeConstants.SERVER_RELATIVE );
    }

    @Override
    public String generateBaseUrl()
    {
        if ( "".equals( portalRequest.getBaseUri() ) )
        {
            return "/api";
        }

        final StringBuilder result = new StringBuilder( portalRequest.getBaseUri() );
        UrlBuilderHelper.appendPart( result, "_" );
        return UrlBuilderHelper.rewriteUri( portalRequest.getRawRequest(), urlType, result.toString() );
    }

    public static Builder create()
    {
        return new Builder();
    }

    static class Builder
    {
        private PortalRequest portalRequest;

        private String urlType;

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

        public NonSiteRequestBaseUrlStrategy build()
        {
            return new NonSiteRequestBaseUrlStrategy( this );
        }
    }
}
