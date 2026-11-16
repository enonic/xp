package com.enonic.xp.web.jetty.impl.configurator;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

import org.eclipse.jetty.http.HttpFields;
import org.eclipse.jetty.server.ConnectionMetaData;
import org.eclipse.jetty.server.ForwardedRequestCustomizer;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.util.InetAddressSet;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TrustedProxyForwardedCustomizerTest
{
    private final HttpConfiguration.Customizer delegate = mock( HttpConfiguration.Customizer.class );

    private final Request request = mock( Request.class );

    private final Request customized = mock( Request.class );

    private final HttpFields.Mutable responseHeaders = HttpFields.build();

    private TrustedProxyForwardedCustomizer newCustomizer( final String... trustedProxies )
    {
        final InetAddressSet addresses = new InetAddressSet();
        for ( final String proxy : trustedProxies )
        {
            addresses.add( proxy );
        }
        when( delegate.customize( same( request ), any() ) ).thenReturn( customized );
        return new TrustedProxyForwardedCustomizer( delegate, addresses );
    }

    private void remoteAddress( final SocketAddress address )
    {
        final ConnectionMetaData connectionMetaData = mock( ConnectionMetaData.class );
        when( connectionMetaData.getRemoteSocketAddress() ).thenReturn( address );
        when( request.getConnectionMetaData() ).thenReturn( connectionMetaData );
    }

    @Test
    void trustedPeerIsForwarded()
    {
        remoteAddress( new InetSocketAddress( "10.20.30.40", 41234 ) );

        assertSame( customized, newCustomizer( "10.0.0.0/8" ).customize( request, responseHeaders ) );
    }

    @Test
    void untrustedPeerIsNotForwarded()
    {
        remoteAddress( new InetSocketAddress( "203.0.113.9", 41234 ) );

        assertSame( request, newCustomizer( "10.0.0.0/8", "127.0.0.1" ).customize( request, responseHeaders ) );
        verify( delegate, never() ).customize( any(), any() );
    }

    @Test
    void unresolvedPeerIsNotForwarded()
    {
        remoteAddress( InetSocketAddress.createUnresolved( "proxy.invalid", 41234 ) );

        assertSame( request, newCustomizer( "10.0.0.0/8" ).customize( request, responseHeaders ) );
    }

    @Test
    void nonInetPeerIsNotForwarded()
    {
        remoteAddress( mock( SocketAddress.class ) );

        assertSame( request, newCustomizer( "10.0.0.0/8" ).customize( request, responseHeaders ) );
    }

    @Test
    void emptyTrustedProxiesTrustsEveryPeer()
    {
        assertInstanceOf( ForwardedRequestCustomizer.class, TrustedProxyForwardedCustomizer.from( "" ) );
        assertInstanceOf( ForwardedRequestCustomizer.class, TrustedProxyForwardedCustomizer.from( null ) );
        assertInstanceOf( TrustedProxyForwardedCustomizer.class, TrustedProxyForwardedCustomizer.from( "10.0.0.0/8" ) );
    }

    @Test
    void invalidTrustedProxyIsRejected()
    {
        assertThrows( IllegalArgumentException.class, () -> TrustedProxyForwardedCustomizer.from( "not an address" ) );
    }
}
