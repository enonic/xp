package com.enonic.xp.impl.server.rest.model;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ExportNodesRequestJson
{
    private final RepoPath sourceRepoPath;

    private final String exportName;

    private final boolean archive;

    public ExportNodesRequestJson( @JsonProperty("sourceRepoPath") final String sourceRepoPath, //
                                   @JsonProperty("exportName") final String exportName, //
                                   @JsonProperty("archive") final Boolean archive )
    {
        Objects.requireNonNull( sourceRepoPath, "sourceRepoPath is required" );
        Objects.requireNonNull( exportName, "exportName is required" );

        this.sourceRepoPath = RepoPath.from( sourceRepoPath );
        this.exportName = exportName;
        this.archive = archive != null ? archive : false;
    }

    public RepoPath getSourceRepoPath()
    {
        return sourceRepoPath;
    }

    public String getExportName()
    {
        return exportName;
    }

    public boolean isArchive()
    {
        return archive;
    }
}
