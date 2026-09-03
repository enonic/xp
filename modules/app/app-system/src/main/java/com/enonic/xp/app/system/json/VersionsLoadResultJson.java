package com.enonic.xp.app.system.json;

import java.util.List;

import com.enonic.xp.dump.VersionsLoadResult;

public record VersionsLoadResultJson(Long successful, List<LoadErrorJson> errors)
{
    public static VersionsLoadResultJson from( final VersionsLoadResult result )
    {
        return new VersionsLoadResultJson( result.getSuccessful(), result.getErrors().stream().map( LoadErrorJson::from ).toList() );
    }
}
