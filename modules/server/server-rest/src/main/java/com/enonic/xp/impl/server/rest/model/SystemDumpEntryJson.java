package com.enonic.xp.impl.server.rest.model;

import java.time.Instant;

import org.jspecify.annotations.NonNull;

import com.enonic.xp.dump.SystemDumpEntry;
import com.enonic.xp.impl.server.rest.ModelToStringHelper;

public record SystemDumpEntryJson(String name, Instant timestamp, String xpVersion, String modelVersion, long size,
                                  SystemDumpResultJson repositories)
{
    public static SystemDumpEntryJson from( final SystemDumpEntry entry )
    {
        return new SystemDumpEntryJson( entry.name(), entry.timestamp(), entry.xpVersion(),
                                        entry.modelVersion() == null ? null : entry.modelVersion().toString(), entry.size(),
                                        entry.systemDumpResult() == null ? null : SystemDumpResultJson.from( entry.systemDumpResult() ) );
    }

    @Override
    public @NonNull String toString()
    {
        return ModelToStringHelper.convertToString( this );
    }
}
