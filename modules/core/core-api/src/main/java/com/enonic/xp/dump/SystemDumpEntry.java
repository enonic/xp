package com.enonic.xp.dump;

import java.time.Instant;
import java.util.Objects;

import com.enonic.xp.util.Version;

public record SystemDumpEntry(String name, Instant timestamp, String xpVersion, Version modelVersion, long size,
                              SystemDumpResult systemDumpResult)
{
    public SystemDumpEntry
    {
        Objects.requireNonNull( name, "name cannot be null" );
    }

}
