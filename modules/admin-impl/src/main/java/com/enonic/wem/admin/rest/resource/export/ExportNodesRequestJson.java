package com.enonic.wem.admin.rest.resource.export;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Preconditions;

class ExportNodesRequestJson
{
    private final RepoPath sourceRepoPath;

    private final String targetDirectory;

    private final boolean includeIds;

    private final boolean dryRun;


    public ExportNodesRequestJson( @JsonProperty("sourceRepoPath") final String sourceRepoPath, //
                                   @JsonProperty("targetDirectory") final String targetDirectory, //
                                   @JsonProperty("importWithIds") final Boolean includeIds, //
                                   @JsonProperty("dryRun") final Boolean dryRun )
    {
        Preconditions.checkNotNull( sourceRepoPath, "sourceRepoPath not given" );
        Preconditions.checkNotNull( targetDirectory, "targetDirectory not given" );

        this.sourceRepoPath = RepoPath.from( sourceRepoPath );
        this.targetDirectory = targetDirectory;
        this.includeIds = includeIds != null ? includeIds : true;
        this.dryRun = dryRun != null ? dryRun : false;
    }

    public RepoPath getSourceRepoPath()
    {
        return sourceRepoPath;
    }

    public String getTargetDirectory()
    {
        return targetDirectory;
    }

    public boolean isIncludeIds()
    {
        return includeIds;
    }

    public boolean isDryRun()
    {
        return dryRun;
    }
}
