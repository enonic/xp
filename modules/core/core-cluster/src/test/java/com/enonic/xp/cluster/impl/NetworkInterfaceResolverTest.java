package com.enonic.xp.cluster.impl;

import org.junit.Test;

import static org.junit.Assert.*;

public class NetworkInterfaceResolverTest
{
    @Test
    public void resolveInterface()
    {
        final NetworkInterfaceResolver resolver = new NetworkInterfaceResolver();
//        final String localIpv6 = resolver.resolveAddress( "_lo0:ipv6_" );
//        final String localIpv4 = resolver.resolveAddress( "_lo0:ipv4_" );
        final String localGeneric = resolver.resolveAddress( "_local_" );
        final String ipAddress = resolver.resolveAddress( "192.168.0.1" );

//        assertEquals( "127.0.0.1", localIpv4 );
//        assertEquals( "0:0:0:0:0:0:0:1", localIpv6 );
        assertEquals( "127.0.0.1", localGeneric );
        assertEquals( "192.168.0.1", ipAddress );
    }

}