package com.enonic.xp.portal.impl.url;

import java.util.function.Supplier;

import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalRequestAccessor;
import com.enonic.xp.portal.url.IdentityUrlParams;
import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.web.vhost.VirtualHost;
import com.enonic.xp.web.vhost.VirtualHostHelper;

import static java.util.Objects.requireNonNullElseGet;

final class IdentityPathSupplier
    implements Supplier<String>
{
    private final IdentityUrlParams params;

    IdentityPathSupplier( IdentityUrlParams params )
    {
        this.params = params;
    }

    @Override
    public String get()
    {
        final StringBuilder url = new StringBuilder();

        UrlBuilderHelper.appendSubPath( url, "idprovider" );

        final IdProviderKey idProviderKey = requireNonNullElseGet( this.params.getIdProviderKey(), () -> {
            final PortalRequest portalRequest = PortalRequestAccessor.get();
            final VirtualHost virtualHost = VirtualHostHelper.getVirtualHost( portalRequest.getRawRequest() );
            return virtualHost.getDefaultIdProviderKey();
        } );

        UrlBuilderHelper.appendPart( url, idProviderKey.toString() );

        final String idProviderFunction = this.params.getIdProviderFunction();
        if ( idProviderFunction != null )
        {
            UrlBuilderHelper.appendPart( url, idProviderFunction );
        }

        return url.toString();
    }
}
