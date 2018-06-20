package com.enonic.xp.ignite.impl.config;

import org.junit.Test;

import static org.junit.Assert.*;

public class NetworkInterfaceResolverTest
{
    @Test
    public void resolveInterface()
    {
        final NetworkInterfaceResolver resolver = new NetworkInterfaceResolver();
        final String localIpv6 = resolver.resolveAddress( "lo0:ipv6" );
        final String localIpv4 = resolver.resolveAddress( "lo0:ipv4" );
        final String localGeneric = resolver.resolveAddress( "local" );

        assertEquals( "127.0.0.1", localIpv4 );
        assertEquals( "0:0:0:0:0:0:0:1", localIpv6 );
        assertEquals( "127.0.0.1", localGeneric );
    }

}