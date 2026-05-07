package com.enonic.xp.portal.impl.url;

import java.util.function.Supplier;

import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalRequestAccessor;
import com.enonic.xp.web.vhost.VirtualHost;
import com.enonic.xp.web.vhost.VirtualHostHelper;

import static java.util.Objects.requireNonNull;

final class IdentityBaseUrlSupplier
    implements Supplier<String>
{
    private final String urlType;

    IdentityBaseUrlSupplier( final String urlType )
    {
        this.urlType = urlType;
    }

    @Override
    public String get()
    {
        final PortalRequest portalRequest = requireNonNull( PortalRequestAccessor.get(), "no request bound" );

        final VirtualHost virtualHost = VirtualHostHelper.getVirtualHost( portalRequest.getRawRequest() );

        final StringBuilder url = new StringBuilder();

        UrlBuilderHelper.appendSubPath( url, virtualHost.getTarget() );
        UrlBuilderHelper.appendSubPath( url, "_" );

        return UrlBuilderHelper.rewriteUri( portalRequest.getRawRequest(), urlType, url.toString() );
    }
}
