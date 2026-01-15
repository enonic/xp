package com.enonic.xp.impl.server.rest.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Preconditions;

public class ExportNodesRequestJson
{
    private final RepoPath sourceRepoPath;

    private final String exportName;

    private final boolean exportWithIds;

    private final boolean dryRun;

    private final boolean includeVersions;

    private final Integer batchSize;

    public ExportNodesRequestJson( @JsonProperty("sourceRepoPath") final String sourceRepoPath, //
                                   @JsonProperty("exportName") final String exportName, //
                                   @JsonProperty("exportWithIds") final Boolean exportWithIds, //
                                   @JsonProperty("includeVersions") final Boolean includeVersions, //
                                   @JsonProperty("dryRun") final Boolean dryRun, //
                                   @JsonProperty("batchSize") final Integer batchSize )
    {
        Preconditions.checkNotNull( sourceRepoPath, "sourceRepoPath not given" );
        Preconditions.checkNotNull( exportName, "exportName not given" );

        this.sourceRepoPath = RepoPath.from( sourceRepoPath );
        this.exportName = exportName;
        this.exportWithIds = exportWithIds != null ? exportWithIds : true;
        this.dryRun = dryRun != null ? dryRun : false;
        this.includeVersions = includeVersions != null ? includeVersions : false;
        this.batchSize = batchSize;
    }

    public RepoPath getSourceRepoPath()
    {
        return sourceRepoPath;
    }

    public String getExportName()
    {
        return exportName;
    }

    public boolean isExportWithIds()
    {
        return exportWithIds;
    }

    public boolean isDryRun()
    {
        return dryRun;
    }

    public boolean isIncludeVersions()
    {
        return includeVersions;
    }

    public Integer getBatchSize()
    {
        return batchSize;
    }
}
