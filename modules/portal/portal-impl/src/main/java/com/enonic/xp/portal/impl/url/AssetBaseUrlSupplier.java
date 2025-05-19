package com.enonic.xp.portal.impl.url;

import java.util.function.Supplier;

import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalRequestAccessor;
import com.enonic.xp.project.ProjectName;

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
        final PortalRequest portalRequest = PortalRequestAccessor.get();

        final StringBuilder uriBuilder = new StringBuilder( portalRequest.getBaseUri() );

        if ( portalRequest.isSiteBase() )
        {
            UrlBuilderHelper.appendSubPath( uriBuilder, ProjectName.from( portalRequest.getRepositoryId() ).toString() );
            UrlBuilderHelper.appendSubPath( uriBuilder, portalRequest.getBranch().getValue() );
        }

        UrlBuilderHelper.appendPart( uriBuilder, "_" );

        return UrlBuilderHelper.rewriteUri( portalRequest.getRawRequest(), urlType, uriBuilder.toString() );
    }
}
