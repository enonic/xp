package com.enonic.xp.repo.impl.dump.model;

import java.time.Instant;

import com.enonic.xp.dump.SystemDumpResult;

public class DumpMeta
{
    private final String xpVersion;

    private final Instant timestamp;
    
    private final SystemDumpResult systemDumpResult;
    
    public DumpMeta( final String xpVersion, final Instant timestamp, final SystemDumpResult systemDumpResult )
    {
        this.timestamp = timestamp;
        this.xpVersion = xpVersion;
        this.systemDumpResult = systemDumpResult;
    }

    public String getXpVersion()
    {
        return xpVersion;
    }

    public Instant getTimestamp()
    {
        return timestamp;
    }

    public SystemDumpResult getSystemDumpResult()
    {
        return systemDumpResult;
    }
}
