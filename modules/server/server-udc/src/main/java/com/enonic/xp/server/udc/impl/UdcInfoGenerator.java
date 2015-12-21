package com.enonic.xp.server.udc.impl;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.common.io.BaseEncoding;

import com.enonic.xp.server.ServerInfo;
import com.enonic.xp.server.VersionInfo;

final class UdcInfoGenerator
{
    private final AtomicInteger count;

    public UdcInfoGenerator()
    {
        this.count = new AtomicInteger( 0 );
    }

    public UdcInfo generate()
        throws Exception
    {
        final UdcInfo info = new UdcInfo();
        info.setCount( this.count.incrementAndGet() );
        info.setHardwareAddress( getHardwareAddress() );
        info.setJavaVersion( System.getProperty( "java.version" ) );
        info.setMaxMemory( getMaxMemory() );
        info.setNumCpu( getNumCpu() );
        info.setProduct( "xp" );
        info.setVersion( VersionInfo.get().getVersion() );
        info.setVersionHash( ServerInfo.get().getBuildInfo().getHash() );
        info.setOsName( System.getProperty( "os.name" ) );
        return info;
    }

    private String getHardwareAddress()
        throws Exception
    {
        final InetAddress addr = InetAddress.getLocalHost();
        final NetworkInterface network = NetworkInterface.getByInetAddress( addr );
        final byte[] hardwareAddr = network.getHardwareAddress();
        return BaseEncoding.base16().encode( hardwareAddr );
    }

    private long getMaxMemory()
    {
        return Runtime.getRuntime().maxMemory();
    }

    private int getNumCpu()
    {
        return Runtime.getRuntime().availableProcessors();
    }
}
