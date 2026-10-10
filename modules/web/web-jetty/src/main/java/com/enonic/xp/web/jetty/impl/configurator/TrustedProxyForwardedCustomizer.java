package com.enonic.xp.web.jetty.impl.configurator;

import java.net.InetSocketAddress;

import org.eclipse.jetty.http.HttpFields;
import org.eclipse.jetty.server.ForwardedRequestCustomizer;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.util.InetAddressSet;

import com.google.common.base.Splitter;

final class TrustedProxyForwardedCustomizer
    implements HttpConfiguration.Customizer
{
    private final HttpConfiguration.Customizer delegate;

    private final InetAddressSet trustedProxies;

    TrustedProxyForwardedCustomizer( final HttpConfiguration.Customizer delegate, final InetAddressSet trustedProxies )
    {
        this.delegate = delegate;
        this.trustedProxies = trustedProxies;
    }

    static HttpConfiguration.Customizer from( final String trustedProxies )
    {
        final InetAddressSet addresses = new InetAddressSet();
        Splitter.on( ',' ).trimResults().omitEmptyStrings().split( trustedProxies == null ? "" : trustedProxies ).forEach( addresses::add );
        return addresses.isEmpty()
            ? new ForwardedRequestCustomizer()
            : new TrustedProxyForwardedCustomizer( new ForwardedRequestCustomizer(), addresses );
    }

    @Override
    public Request customize( final Request request, final HttpFields.Mutable responseHeaders )
    {
        return isTrusted( request ) ? delegate.customize( request, responseHeaders ) : request;
    }

    private boolean isTrusted( final Request request )
    {
        return request.getConnectionMetaData().getRemoteSocketAddress() instanceof InetSocketAddress address &&
            address.getAddress() != null && trustedProxies.test( address.getAddress() );
    }
}
