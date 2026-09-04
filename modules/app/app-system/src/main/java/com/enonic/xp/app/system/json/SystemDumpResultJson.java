package com.enonic.xp.app.system.json;

import java.util.List;
import java.util.stream.StreamSupport;

import com.enonic.xp.dump.SystemDumpResult;

public record SystemDumpResultJson(List<RepoDumpResultJson> repositories)
{
    public static SystemDumpResultJson from( final SystemDumpResult systemDumpResult )
    {
        return new SystemDumpResultJson(
            StreamSupport.stream( systemDumpResult.spliterator(), false ).map( RepoDumpResultJson::from ).toList() );
    }

    @Override
    public String toString()
    {
        return JsonHelper.toJson( this );
    }
}
