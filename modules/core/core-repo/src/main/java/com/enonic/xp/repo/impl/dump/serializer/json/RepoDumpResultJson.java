package com.enonic.xp.repo.impl.dump.serializer.json;


import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.dump.BranchDumpResult;
import com.enonic.xp.dump.DumpError;
import com.enonic.xp.dump.RepoDumpResult;
import com.enonic.xp.repository.RepositoryId;

import static java.util.Optional.ofNullable;

public class RepoDumpResultJson
{
    @JsonProperty
    private Long versions;

    @JsonProperty
    private Map<String, BranchDumpResultJson> branchResults;

    @JsonProperty
    private List<String> versionsErrors;

    @SuppressWarnings("unused")
    public RepoDumpResultJson()
    {
    }

    private RepoDumpResultJson( final Long versions, final Map<String, BranchDumpResultJson> branchResults,
                                final List<String> versionsErrors )
    {
        this.versions = versions;
        this.branchResults = branchResults;
        this.versionsErrors = versionsErrors;
    }

    public static RepoDumpResultJson from( final RepoDumpResult repoDumpResult )
    {
        final Map<String, BranchDumpResultJson> branchResults = new HashMap<>();
        repoDumpResult.getBranchResults().forEach( branchResult -> {
            final BranchDumpResultJson branchDumpResultJson = BranchDumpResultJson.from( branchResult );
            branchResults.put( branchResult.getBranch().toString(), branchDumpResultJson );
        } );

        final List<String> versionsErrors =
            repoDumpResult.getVersionsErrors().stream().map( DumpError::getMessage ).collect( Collectors.toList() );

        return new RepoDumpResultJson( repoDumpResult.getVersions(), branchResults, versionsErrors );
    }

    public static RepoDumpResult fromJson( final String repositoryId, RepoDumpResultJson json )
    {
        final RepoDumpResult.Builder repoDumpResult = RepoDumpResult.create( RepositoryId.from( repositoryId ) ).
            versions( json.versions );

        ofNullable( json.getVersionsErrors() ).orElse( Collections.emptyList() ).forEach(
            versionsError -> repoDumpResult.error( DumpError.error( versionsError ) ) );

        json.getBranchResults().forEach( ( key, value ) -> {
            final BranchDumpResult.Builder branchDumpResult = BranchDumpResult.create( Branch.from( key ) ).
                addedNodes( value.getSuccessful() );
            value.
                getErrors().
                stream().
                map( DumpError::new ).
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

    public List<String> getVersionsErrors()
    {
        return versionsErrors;
    }

    public void setVersionsErrors( final List<String> versionsErrors )
    {
        this.versionsErrors = versionsErrors;
    }

}
