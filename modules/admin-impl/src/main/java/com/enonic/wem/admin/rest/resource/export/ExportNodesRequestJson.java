package com.enonic.wem.admin.rest.resource.export;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.wem.api.node.NodePath;

public class ExportNodesRequestJson
{
    private NodePath exportRoot;

    private String exportName;

    private boolean includeIds;

    private boolean dryRun;


    public ExportNodesRequestJson( @JsonProperty("exportNodePath") final String exportNodePath, //
                                   @JsonProperty("exportName") final String exportName, //
                                   @JsonProperty("importWithIds") final Boolean includeIds, //
                                   @JsonProperty("dryRun") final Boolean dryRun )
    {
        this.exportRoot = NodePath.newPath( exportNodePath ).build();
        this.exportName = exportName;
        this.includeIds = includeIds != null ? includeIds : true;
        this.dryRun = dryRun != null ? dryRun : false;
    }

    public NodePath getExportRoot()
    {
        return exportRoot;
    }

    public String getExportName()
    {
        return exportName;
    }

    public boolean isIncludeIds()
    {
        return includeIds;
    }

    public boolean isDryRun()
    {
        return dryRun;
    }
}
