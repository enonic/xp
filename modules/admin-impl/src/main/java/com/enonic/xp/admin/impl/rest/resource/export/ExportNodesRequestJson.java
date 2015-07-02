package com.enonic.xp.admin.impl.rest.resource.export;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Preconditions;

class ExportNodesRequestJson
{
    private final RepoPath sourceRepoPath;

    private final String exportName;

    private final boolean exportWithIds;

    private final boolean dryRun;

    public ExportNodesRequestJson( @JsonProperty("sourceRepoPath") final String sourceRepoPath, //
                                   @JsonProperty("exportName") final String exportName, //
                                   @JsonProperty("exportWithIds") final Boolean exportWithIds, //
                                   @JsonProperty("dryRun") final Boolean dryRun )
    {
        Preconditions.checkNotNull( sourceRepoPath, "sourceRepoPath not given" );
        Preconditions.checkNotNull( exportName, "exportName not given" );

        this.sourceRepoPath = RepoPath.from( sourceRepoPath );
        this.exportName = exportName;
        this.exportWithIds = exportWithIds != null ? exportWithIds : true;
        this.dryRun = dryRun != null ? dryRun : false;
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
}
