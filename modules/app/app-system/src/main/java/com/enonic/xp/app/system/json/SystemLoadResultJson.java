package com.enonic.xp.app.system.json;

import java.util.List;
import java.util.stream.StreamSupport;

import com.enonic.xp.dump.SystemLoadResult;

public record SystemLoadResultJson(List<RepoLoadResultJson> repositories)
{
    public static SystemLoadResultJson from( final SystemLoadResult results )
    {
        return new SystemLoadResultJson( StreamSupport.stream( results.spliterator(), false ).map( RepoLoadResultJson::from ).toList() );
    }

    @Override
    public String toString()
    {
        return JsonHelper.toJson( this );
    }
}
