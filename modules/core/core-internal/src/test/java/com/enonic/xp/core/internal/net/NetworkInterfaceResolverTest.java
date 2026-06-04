package com.enonic.xp.core.internal.net;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class NetworkInterfaceResolverTest
{
    @Test
    void resolveInterface()
    {
        final NetworkInterfaceResolver resolver = new NetworkInterfaceResolver();
        final String localIpv6 = resolver.resolveAddress( "_local:ipv6_" );
        final String localIpv4 = resolver.resolveAddress( "_local:ipv4_" );
        final String localGeneric = resolver.resolveAddress( "_local_" );
        final String ipAddress = resolver.resolveAddress( "192.168.0.1" );
        final String localhost = resolver.resolveAddress( "localhost" );

        assertAll( () -> assertEquals( "127.0.0.1", localIpv4 ), () -> assertEquals( "0:0:0:0:0:0:0:1", localIpv6 ),
                   () -> assertEquals( InetAddress.getLoopbackAddress().getHostAddress(), localGeneric ),
                   () -> assertEquals( "192.168.0.1", ipAddress ), () -> assertEquals( "127.0.0.1", localhost ) );
    }

    @Test
    void resolveLoopbackInterfaceName()
        throws SocketException
    {
        final NetworkInterface loopback =
            NetworkInterface.networkInterfaces().filter( NetworkInterfaceResolverTest::isLoopback ).findFirst().orElseThrow();

        final NetworkInterfaceResolver resolver = new NetworkInterfaceResolver();

        assertAll( () -> assertEquals( InetAddress.getLoopbackAddress().getHostAddress(),
                                       resolver.resolveAddress( "_" + loopback.getName() + "_" ) ),
                   () -> assertEquals( "127.0.0.1", resolver.resolveAddress( "_" + loopback.getName() + ":ipv4_" ) ),
                   () -> assertEquals( "0:0:0:0:0:0:0:1", resolver.resolveAddress( "_" + loopback.getName() + ":ipv6_" ) ) );
    }

    private static boolean isLoopback( final NetworkInterface networkInterface )
    {
        try
        {
            return networkInterface.isLoopback();
        }
        catch ( SocketException e )
        {
            return false;
        }
    }

    @Test
    void resolveAddresses_allFamiliesWhenVersionNotSpecified()
    {
        final NetworkInterfaceResolver resolver = new NetworkInterfaceResolver();
        assertThat( resolver.resolveAddresses( "_local_" ) ).containsExactlyInAnyOrder( "127.0.0.1", "0:0:0:0:0:0:0:1" );
    }

    @Test
    void resolveAddresses_singleAddress()
    {
        final NetworkInterfaceResolver resolver = new NetworkInterfaceResolver();
        assertAll( () -> assertThat( resolver.resolveAddresses( "192.168.0.1" ) ).containsExactly( "192.168.0.1" ),
                   () -> assertThat( resolver.resolveAddresses( "_local:ipv4_" ) ).containsExactly( "127.0.0.1" ),
                   () -> assertThat( resolver.resolveAddresses( "_local:ipv6_" ) ).containsExactly( "0:0:0:0:0:0:0:1" ) );
    }

    @Test
    void resolveAddresses_auto()
    {
        final NetworkInterfaceResolver resolver = new NetworkInterfaceResolver( () -> "_local_" );
        assertThat( resolver.resolveAddresses( "_auto_" ) ).containsExactlyInAnyOrder( "127.0.0.1", "0:0:0:0:0:0:0:1" );
    }

    @Test
    void resolveAuto()
    {
        final NetworkInterfaceResolver devResolver = new NetworkInterfaceResolver( () -> "_local_" );
        final NetworkInterfaceResolver prodResolver = new NetworkInterfaceResolver( () -> "0.0.0.0" );

        assertAll( () -> assertEquals( "127.0.0.1", devResolver.resolveAddress( "_auto_" ) ),
                   () -> assertEquals( "0.0.0.0", prodResolver.resolveAddress( "_auto_" ) ) );
    }

    @Test
    void resolveAutoWithoutCallback()
    {
        final NetworkInterfaceResolver resolver = new NetworkInterfaceResolver();
        assertThatThrownBy( () -> resolver.resolveAddress( "_auto_" ) ).isInstanceOf( IllegalArgumentException.class );
    }

    @Test
    void resolveAutoCallbackReturningAuto()
    {
        final NetworkInterfaceResolver resolver = new NetworkInterfaceResolver( () -> "_auto_" );
        assertThatThrownBy( () -> resolver.resolveAddress( "_auto_" ) ).isInstanceOf( IllegalArgumentException.class );
    }
}