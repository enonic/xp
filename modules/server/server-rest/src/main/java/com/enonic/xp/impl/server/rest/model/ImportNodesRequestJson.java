package com.enonic.xp.impl.server.rest.model;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import static java.util.Objects.requireNonNull;

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
                                   @JsonProperty("importWithPermissions") final Boolean importWithPermissions, //
                                   @JsonProperty("xslSource") final String xslSource, //
                                   @JsonProperty("xslParams") final Map<String, Object> xslParams )
    {
        requireNonNull( exportName, "exportName is required" );
        requireNonNull( targetRepoPath, "targetRepoPath is required" );

        // Older clients still send these keys; XSL transformation of server-side imports is not supported.
        Preconditions.checkArgument( Strings.isNullOrEmpty( xslSource ) && ( xslParams == null || xslParams.isEmpty() ),
                                     "xslSource and xslParams are not supported" );

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
