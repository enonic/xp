package com.enonic.xp.cluster.impl;

import org.junit.jupiter.api.Test;

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
                   () -> assertEquals( "127.0.0.1", localGeneric ), () -> assertEquals( "192.168.0.1", ipAddress ),
                   () -> assertEquals( "127.0.0.1", localhost ) );
    }

}
