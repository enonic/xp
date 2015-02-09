package com.enonic.wem.admin.rest.resource.export;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.wem.api.node.NodePath;

public class ImportNodesRequestJson
{
    private String exportFilePath;

    private NodePath targetNodePath;

    private boolean dryRun;

    private boolean importWithIds = true;

    @JsonCreator
    public ImportNodesRequestJson( @JsonProperty("exportFilePath") final String exportFilePath, //
                                   @JsonProperty("targetNodePath") final String targetNodePath, //
                                   @JsonProperty("importWithIds") final Boolean importWithIds, //
                                   @JsonProperty("dryRun") final Boolean dryRun )

    {
        this.exportFilePath = exportFilePath;
        this.targetNodePath = NodePath.newPath( targetNodePath ).build();
        this.dryRun = dryRun != null ? dryRun : false;
        this.importWithIds = importWithIds != null ? importWithIds : true;
    }

    public String getExportFilePath()
    {
        return exportFilePath;
    }

    public NodePath getTargetNodePath()
    {
        return targetNodePath;
    }

    public boolean isDryRun()
    {
        return dryRun;
    }

    public boolean isImportWithIds()
    {
        return importWithIds;
    }
}


