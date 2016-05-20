package com.enonic.xp.server.udc.impl;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.common.io.BaseEncoding;

import com.enonic.xp.server.ServerInfo;
import com.enonic.xp.server.VersionInfo;

final class UdcInfoGenerator
{
    private final AtomicInteger count;

    private final long startTime;

    UdcInfoGenerator()
    {
        this.count = new AtomicInteger( 0 );
        this.startTime = System.currentTimeMillis();
    }

    UdcInfo generate()
        throws Exception
    {
        final UdcInfo info = new UdcInfo();
        info.hardwareAddress = getHardwareAddress();
        info.javaVersion = System.getProperty( "java.version" );
        info.maxMemory = getMaxMemory();
        info.numCpu = getNumCpu();
        info.product = "xp";
        info.version = VersionInfo.get().getVersion();
        info.versionHash = ServerInfo.get().getBuildInfo().getHash();
        info.osName = System.getProperty( "os.name" );
        info.timezone = TimeZone.getDefault().getID();
        info.count = this.count.incrementAndGet();
        info.upTime = System.currentTimeMillis() - this.startTime;
        return info;
    }

    private String getHardwareAddress()
        throws Exception
    {
        final InetAddress addr = InetAddress.getLocalHost();
        final NetworkInterface network = NetworkInterface.getByInetAddress( addr );

        if ( network != null )
        {
            final byte[] hardwareAddr = network.getHardwareAddress();
            if ( hardwareAddr != null )
            {
                return BaseEncoding.base16().encode( hardwareAddr );
            }
        }

        return "unknown";
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
