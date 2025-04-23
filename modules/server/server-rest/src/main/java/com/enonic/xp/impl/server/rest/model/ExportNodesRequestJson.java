package com.enonic.xp.impl.server.rest.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Preconditions;

public class ExportNodesRequestJson
{
    private final RepoPath sourceRepoPath;

    private final String exportName;

    private final boolean exportWithIds;

    private final boolean includeVersions;

    public ExportNodesRequestJson( @JsonProperty("sourceRepoPath") final String sourceRepoPath, //
                                   @JsonProperty("exportName") final String exportName, //
                                   @JsonProperty("exportWithIds") final Boolean exportWithIds, //
                                   @JsonProperty("includeVersions") final Boolean includeVersions )
    {
        Preconditions.checkNotNull( sourceRepoPath, "sourceRepoPath not given" );
        Preconditions.checkNotNull( exportName, "exportName not given" );

        this.sourceRepoPath = RepoPath.from( sourceRepoPath );
        this.exportName = exportName;
        this.exportWithIds = exportWithIds != null ? exportWithIds : true;
        this.includeVersions = includeVersions != null ? includeVersions : false;
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

    @Deprecated
    public boolean isDryRun()
    {
        return false;
    }

    public boolean isIncludeVersions()
    {
        return includeVersions;
    }
}
