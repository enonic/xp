package com.enonic.xp.impl.server.rest.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import static java.util.Objects.requireNonNull;

// xslSource and xslParams are deprecated and ignored: XSLT files in the exports directory are not supported.
@JsonIgnoreProperties({"xslSource", "xslParams"})
public final class ImportNodesRequestJson
{
    private final RepoPath targetRepoPath;

    private final String exportName;

    private final boolean importWithIds;

    private final boolean importWithPermissions;

    @JsonCreator
    public ImportNodesRequestJson( @JsonProperty("exportName") final String exportName, //
                                   @JsonProperty("targetRepoPath") final String targetRepoPath, //
                                   @JsonProperty("importWithIds") final Boolean importWithIds, //
                                   @JsonProperty("importWithPermissions") final Boolean importWithPermissions )
    {
        requireNonNull( exportName, "exportName is required" );
        requireNonNull( targetRepoPath, "targetRepoPath is required" );

        this.targetRepoPath = RepoPath.from( targetRepoPath );
        this.exportName = exportName;
        this.importWithIds = importWithIds != null ? importWithIds : true;
        this.importWithPermissions = importWithPermissions != null ? importWithPermissions : true;
    }

    public RepoPath getTargetRepoPath()
    {
        return targetRepoPath;
    }

    public String getExportName()
    {
        return exportName;
    }

    public boolean isImportWithIds()
    {
        return importWithIds;
    }

    public boolean isImportWithPermissions()
    {
        return importWithPermissions;
    }
}
