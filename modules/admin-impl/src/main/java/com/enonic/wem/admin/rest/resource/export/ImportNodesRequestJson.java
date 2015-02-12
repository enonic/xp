package com.enonic.wem.admin.rest.resource.export;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Preconditions;

public class ImportNodesRequestJson
{
    private final RepoPath targetRepoPath;

    private final String sourceDirectory;

    private final boolean dryRun;

    private final boolean importWithIds;

    @JsonCreator
    public ImportNodesRequestJson( @JsonProperty("sourceDirectory") final String sourceDirectory, //
                                   @JsonProperty("targetRepoPath") final String targetRepoPath, //
                                   @JsonProperty("importWithIds") final Boolean importWithIds, //
                                   @JsonProperty("dryRun") final Boolean dryRun )

    {

        Preconditions.checkNotNull( sourceDirectory, "sourceDirectory not given" );
        Preconditions.checkNotNull( targetRepoPath, "targetRepoPath not given" );

        this.targetRepoPath = RepoPath.from( targetRepoPath );
        this.sourceDirectory = sourceDirectory;
        this.dryRun = dryRun != null ? dryRun : false;
        this.importWithIds = importWithIds != null ? importWithIds : true;
    }

    public RepoPath getTargetRepoPath()
    {
        return targetRepoPath;
    }

    public String getSourceDirectory()
    {
        return sourceDirectory;
    }

    public boolean isDryRun()
    {
        return dryRun;
    }

    public boolean isImportWithIds()
    {
        return importWithIds;
    }
}


