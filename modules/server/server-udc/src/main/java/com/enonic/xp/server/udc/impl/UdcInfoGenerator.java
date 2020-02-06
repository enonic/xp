package com.enonic.xp.server.udc.impl;

import java.util.TimeZone;
import java.util.UUID;

import com.enonic.xp.server.ServerInfo;
import com.enonic.xp.server.VersionInfo;

final class UdcInfoGenerator
{
    private final String uuid;

    private final long startTime;

    UdcInfoGenerator()
    {
        this.uuid = UUID.randomUUID().toString().replace( "-", "" );
        this.startTime = System.currentTimeMillis();
    }

    UdcInfo generate()
    {
        final UdcInfo info = new UdcInfo();
        info.uuid = this.uuid;
        info.javaVersion = System.getProperty( "java.version" );
        info.maxMemory = getMaxMemory();
        info.numCpu = getNumCpu();
        info.product = "xp";
        info.version = VersionInfo.get().getVersion();
        info.versionHash = ServerInfo.get().getBuildInfo().getHash();
        info.osName = System.getProperty( "os.name" );
        info.timezone = TimeZone.getDefault().getID();
        info.upTime = System.currentTimeMillis() - this.startTime;
        return info;
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
