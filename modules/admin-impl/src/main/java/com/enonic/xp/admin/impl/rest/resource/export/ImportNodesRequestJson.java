package com.enonic.xp.admin.impl.rest.resource.export;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Preconditions;

public class ImportNodesRequestJson
{
    private final RepoPath targetRepoPath;

    private final String exportName;

    private final boolean dryRun;

    private final boolean importWithIds;

    private final boolean importWithPermissions;

    @JsonCreator
    public ImportNodesRequestJson( @JsonProperty("exportName") final String exportName, //
                                   @JsonProperty("targetRepoPath") final String targetRepoPath, //
                                   @JsonProperty("importWithIds") final Boolean importWithIds, //
                                   @JsonProperty("importWithPermissions") final Boolean importWithPermissions, //
                                   @JsonProperty("dryRun") final Boolean dryRun )

    {

        Preconditions.checkNotNull( exportName, "exportName not given" );
        Preconditions.checkNotNull( targetRepoPath, "targetRepoPath not given" );

        this.targetRepoPath = RepoPath.from( targetRepoPath );
        this.exportName = exportName;
        this.dryRun = dryRun != null ? dryRun : false;
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

    public boolean isDryRun()
    {
        return dryRun;
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
