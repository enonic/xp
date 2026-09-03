package com.enonic.xp.app.system.json;

import java.util.List;

import com.enonic.xp.dump.BranchLoadResult;

public record BranchLoadResultJson(String branch, Long successful, List<LoadErrorJson> errors)
{
    public static BranchLoadResultJson from( final BranchLoadResult result )
    {
        return new BranchLoadResultJson( result.getBranch().toString(), result.getSuccessful(),
                                         result.getErrors().stream().map( LoadErrorJson::from ).toList() );
    }
}
