package com.enonic.xp.app.system.json;

import java.util.List;
import java.util.stream.StreamSupport;

import com.enonic.xp.dump.RepoLoadResult;

public record RepoLoadResultJson(List<BranchLoadResultJson> branches, String repository, VersionsLoadResultJson versions)
{
    public static RepoLoadResultJson from( final RepoLoadResult results )
    {
        return new RepoLoadResultJson( StreamSupport.stream( results.spliterator(), false ).map( BranchLoadResultJson::from ).toList(),
                                       results.getRepositoryId().toString(), VersionsLoadResultJson.from( results.getVersionsLoadResult() ) );
    }
}
