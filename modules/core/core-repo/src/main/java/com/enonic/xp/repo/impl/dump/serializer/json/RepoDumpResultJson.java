package com.enonic.xp.repo.impl.dump.serializer.json;


import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.dump.BranchDumpResult;
import com.enonic.xp.dump.DumpError;
import com.enonic.xp.dump.RepoDumpResult;
import com.enonic.xp.repository.RepositoryId;

public class RepoDumpResultJson
{
    @JsonProperty
    private Long versions;

    @JsonProperty
    private Map<String, BranchDumpResultJson> branchResults;

    @SuppressWarnings("unused")
    public RepoDumpResultJson()
    {
    }

    private RepoDumpResultJson( final Long versions, final Map<String, BranchDumpResultJson> branchResults )
    {
        this.versions = versions;
        this.branchResults = branchResults;
    }

    public static RepoDumpResultJson from( final RepoDumpResult repoDumpResult )
    {
        final Map<String, BranchDumpResultJson> branchResults = new HashMap<>();
        repoDumpResult.getBranchResults().forEach( branchResult -> {
            final BranchDumpResultJson branchDumpResultJson = BranchDumpResultJson.from( branchResult );
            branchResults.put( branchResult.getBranch().toString(), branchDumpResultJson );
        } );
        return new RepoDumpResultJson( repoDumpResult.getVersions(), branchResults );
    }

    public static RepoDumpResult fromJson( final String repositoryId, RepoDumpResultJson json )
    {
        final RepoDumpResult.Builder repoDumpResult = RepoDumpResult.create( RepositoryId.from( repositoryId ) ).
            versions( json.versions );

        json.getBranchResults().entrySet().forEach( entry -> {
            final BranchDumpResult.Builder branchDumpResult = BranchDumpResult.create( Branch.from( entry.getKey() ) ).
                addedNodes( entry.getValue().getSuccessful() );
            entry.getValue().
                getErrors().
                stream().
                map( error -> new DumpError( error ) ).
                forEach( branchDumpResult::error );
            repoDumpResult.add( branchDumpResult.build() );
        } );

        return repoDumpResult.build();
    }

    public Long getVersions()
    {
        return versions;
    }

    public void setVersions( final Long versions )
    {
        this.versions = versions;
    }

    public Map<String, BranchDumpResultJson> getBranchResults()
    {
        return branchResults;
    }

    public void setBranchResults( final Map<String, BranchDumpResultJson> branchResults )
    {
        this.branchResults = branchResults;
    }
}
