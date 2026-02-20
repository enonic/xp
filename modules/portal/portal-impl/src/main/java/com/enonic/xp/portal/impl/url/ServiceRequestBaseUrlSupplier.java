package com.enonic.xp.portal.impl.url;

import java.util.Objects;
import java.util.function.Supplier;

import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalRequestAccessor;
import com.enonic.xp.portal.impl.PortalRequestHelper;
import com.enonic.xp.portal.url.UrlTypeConstants;
import com.enonic.xp.project.ProjectName;

final class ServiceRequestBaseUrlSupplier
    implements Supplier<String>
{
    private final String urlType;

    private ServiceRequestBaseUrlSupplier( final Builder builder )
    {
        this.urlType = Objects.requireNonNullElse( builder.urlType, UrlTypeConstants.SERVER_RELATIVE );
    }

    @Override
    public String get()
    {
        final PortalRequest portalRequest = Objects.requireNonNull( PortalRequestAccessor.get(), "no request bound" );

        final StringBuilder uriBuilder = new StringBuilder();

        UrlBuilderHelper.appendSubPath( uriBuilder, portalRequest.getBaseUri() );

        if ( PortalRequestHelper.isSiteBase( portalRequest ) )
        {
            UrlBuilderHelper.appendSubPath( uriBuilder, ProjectName.from( portalRequest.getRepositoryId() ).toString() );
            UrlBuilderHelper.appendSubPath( uriBuilder, portalRequest.getBranch().getValue() );
            UrlBuilderHelper.appendAndEncodePathParts( uriBuilder, portalRequest.getContentPath().toString() );
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
        private String urlType;

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
