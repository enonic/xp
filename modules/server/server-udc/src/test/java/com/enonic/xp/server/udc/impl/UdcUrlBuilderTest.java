package com.enonic.xp.server.udc.impl;

import java.net.URL;

import org.junit.Assert;
import org.junit.Test;

public class UdcUrlBuilderTest
{
    @Test
    public void buildUrl()
        throws Exception
    {
        final UdcInfo info = new UdcInfo();
        info.setProduct( "xp" );
        info.setVersion( "1.0.0" );
        info.setVersionHash( "01234abcd" );
        info.setNumCpu( 2 );
        info.setMaxMemory( 1024 * 1024 );
        info.setJavaVersion( "1.8.0" );
        info.setCount( 2 );
        info.setHardwareAddress( "abcd" );
        info.setOsName( "windows" );

        final UdcUrlBuilder builder = new UdcUrlBuilder( "https://udc.enonic.com" );
        final URL url = builder.build( info );

        Assert.assertEquals(
            "https://udc.enonic.com?product=xp&hardware_address=abcd&java_version=1.8.0&os_name=windows&version_hash=01234abcd&ping_count=2&version=1.0.0&num_cpu=2&max_memory=1048576",
            url.toString() );
    }
}
