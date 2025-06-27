package com.enonic.xp.portal.impl.url;

import java.util.Objects;
import java.util.function.Supplier;

import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.impl.PortalRequestHelper;
import com.enonic.xp.portal.url.UrlTypeConstants;
import com.enonic.xp.project.ProjectName;

final class ServiceRequestBaseUrlSupplier
    implements Supplier<String>
{
    private final PortalRequest portalRequest;

    private final String urlType;

    private ServiceRequestBaseUrlSupplier( final Builder builder )
    {
        this.portalRequest = Objects.requireNonNull( builder.portalRequest );
        this.urlType = Objects.requireNonNullElse( builder.urlType, UrlTypeConstants.SERVER_RELATIVE );
    }

    @Override
    public String get()
    {
        final StringBuilder uriBuilder = new StringBuilder( portalRequest.getBaseUri() );

        if ( PortalRequestHelper.isSiteBase( portalRequest ) )
        {
            UrlBuilderHelper.appendSubPath( uriBuilder, ProjectName.from( portalRequest.getRepositoryId() ).toString() );
            UrlBuilderHelper.appendSubPath( uriBuilder, portalRequest.getBranch().getValue() );
            UrlBuilderHelper.appendAndEncodePathParts( uriBuilder, this.portalRequest.getContentPath().toString() );
        }

        UrlBuilderHelper.appendPart( uriBuilder, "_" );

        return UrlBuilderHelper.rewriteUri( portalRequest.getRawRequest(), urlType, uriBuilder.toString() );
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

        public ServiceRequestBaseUrlSupplier build()
        {
            return new ServiceRequestBaseUrlSupplier( this );
        }
    }
}
