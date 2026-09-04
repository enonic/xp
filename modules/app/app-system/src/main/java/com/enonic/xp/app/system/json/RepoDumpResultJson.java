package com.enonic.xp.app.system.json;

import java.util.List;
import java.util.stream.StreamSupport;

import com.enonic.xp.dump.RepoDumpResult;

public record RepoDumpResultJson(List<BranchDumpResultJson> branches, String repositoryId, Long versions, List<DumpErrorJson> versionsErrors)
{
    public static RepoDumpResultJson from( final RepoDumpResult repoDumpResult )
    {
        return new RepoDumpResultJson( StreamSupport.stream( repoDumpResult.spliterator(), false ).map( BranchDumpResultJson::from ).toList(),
                                       repoDumpResult.getRepositoryId().toString(), repoDumpResult.getVersions(),
                                       repoDumpResult.getVersionsErrors().stream().map( DumpErrorJson::from ).toList() );
    }
}
