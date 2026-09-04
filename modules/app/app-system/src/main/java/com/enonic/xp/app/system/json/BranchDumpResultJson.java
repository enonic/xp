package com.enonic.xp.app.system.json;

import java.util.List;

import com.enonic.xp.dump.BranchDumpResult;

public record BranchDumpResultJson(String branch, Long successful, List<DumpErrorJson> errors)
{
    public static BranchDumpResultJson from( final BranchDumpResult result )
    {
        return new BranchDumpResultJson( result.getBranch().toString(), result.getSuccessful(),
                                         result.getErrors().stream().map( DumpErrorJson::from ).toList() );
    }
}
