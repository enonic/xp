package com.enonic.xp.impl.server.rest.model;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

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
        Objects.requireNonNull( sourceRepoPath, "sourceRepoPath is required" );
        Objects.requireNonNull( exportName, "exportName is required" );

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

    public boolean isIncludeVersions()
    {
        return includeVersions;
    }
}
