package com.enonic.xp.repo.impl.dump.model;

import java.time.Instant;

public class DumpMeta
{
    private final String xpVersion;

    private final Instant timestamp;

    public DumpMeta( final String xpVersion )
    {
        this.xpVersion = xpVersion;
        this.timestamp = Instant.now();
    }

    public DumpMeta( final String xpVersion, final Instant timestamp )
    {
        this.timestamp = timestamp;
        this.xpVersion = xpVersion;
    }

    public String getXpVersion()
    {
        return xpVersion;
    }

    public Instant getTimestamp()
    {
        return timestamp;
    }
}
