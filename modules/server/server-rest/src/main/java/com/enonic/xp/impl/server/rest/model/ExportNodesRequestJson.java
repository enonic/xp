package com.enonic.xp.impl.server.rest.model;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ExportNodesRequestJson
{
    private final RepoPath sourceRepoPath;

    private final String exportName;

    private final Integer batchSize;

    public ExportNodesRequestJson( @JsonProperty("sourceRepoPath") final String sourceRepoPath, //
                                   @JsonProperty("exportName") final String exportName, //
                                   @JsonProperty("batchSize") final Integer batchSize )
    {
        Objects.requireNonNull( sourceRepoPath, "sourceRepoPath is required" );
        Objects.requireNonNull( exportName, "exportName is required" );

        this.sourceRepoPath = RepoPath.from( sourceRepoPath );
        this.exportName = exportName;
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


    public Integer getBatchSize()
    {
        return batchSize;
    }
}
