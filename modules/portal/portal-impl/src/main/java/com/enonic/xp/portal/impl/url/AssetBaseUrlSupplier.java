package com.enonic.xp.portal.impl.url;

import java.util.Objects;
import java.util.function.Supplier;

import jakarta.servlet.http.HttpServletRequest;

import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalRequestAccessor;
import com.enonic.xp.portal.impl.PortalRequestHelper;
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.web.servlet.ServletRequestUrlHelper;
import com.enonic.xp.web.servlet.UriRewritingResult;
import com.enonic.xp.web.vhost.VirtualHost;
import com.enonic.xp.web.vhost.VirtualHostHelper;

final class AssetBaseUrlSupplier
    implements Supplier<String>
{
    private final String urlType;

    AssetBaseUrlSupplier( final String urlType )
    {
        this.urlType = urlType;
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
        }

        UrlBuilderHelper.appendPart( uriBuilder, "_" );

        final HttpServletRequest rawRequest = portalRequest.getRawRequest();

        UriRewritingResult rewritingResult = ServletRequestUrlHelper.rewriteUri( rawRequest, uriBuilder.toString() );

        if ( rewritingResult.isOutOfScope() )
        {
            uriBuilder.setLength( 0 );

            final VirtualHost vhost = VirtualHostHelper.getVirtualHost( rawRequest );
            uriBuilder.append( vhost.getTarget() );
            UrlBuilderHelper.appendPart( uriBuilder, "_" );
            rewritingResult = ServletRequestUrlHelper.rewriteUri( rawRequest, uriBuilder.toString() );
        }

        return UrlBuilderHelper.buildServerUrl( rawRequest, urlType ) + rewritingResult.getRewrittenUri();
    }
}
