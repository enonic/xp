package com.enonic.xp.impl.server.rest.model;

import java.util.List;

import org.jspecify.annotations.NonNull;

import com.enonic.xp.dump.SystemDumpEntry;
import com.enonic.xp.impl.server.rest.ModelToStringHelper;

public record SystemDumpListJson(List<SystemDumpEntryJson> dumps)
{
    public static SystemDumpListJson from( final List<SystemDumpEntry> entries )
    {
        return new SystemDumpListJson( entries.stream().map( SystemDumpEntryJson::from ).toList() );
    }

    @Override
    public @NonNull String toString()
    {
        return ModelToStringHelper.convertToString( this );
    }
}
