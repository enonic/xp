package com.enonic.xp.admin.impl.rest.resource.export;

import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;

import com.enonic.xp.core.export.ExportError;
import com.enonic.xp.core.export.NodeExportResult;
import com.enonic.xp.core.node.NodePath;
import com.enonic.xp.core.node.NodePaths;

public class NodeExportResultJson
{
    private int total;

    private boolean dryRun;

    private List<String> exportedNodes;

    private List<String> exportErrors;

    private List<String> exportedBinaries;

    public static NodeExportResultJson from( final NodeExportResult result )
    {
        final NodeExportResultJson nodeExportResultJson = new NodeExportResultJson();

        nodeExportResultJson.total = result.getExportedNodes().getSize();
        nodeExportResultJson.dryRun = result.isDryRun();
        nodeExportResultJson.exportedNodes = exportedNodes( result.getExportedNodes() );
        nodeExportResultJson.exportErrors = exportErrors( result.getExportErrors() );
        nodeExportResultJson.exportedBinaries = result.getExportedBinaries();

        return nodeExportResultJson;
    }

    private static List<String> exportErrors( final List<ExportError> exportErrors )
    {
        List<String> exportErrorList = Lists.newArrayList();

        exportErrorList.addAll( exportErrors.stream().map( ExportError::toString ).collect( Collectors.toList() ) );
        return exportErrorList;
    }

    private static List<String> exportedNodes( final NodePaths nodePaths )
    {
        List<String> exportedNodes = Lists.newArrayList();

        for ( final NodePath nodePath : nodePaths )
        {
            exportedNodes.add( nodePath.toString() );
        }

        return exportedNodes;
    }

    @SuppressWarnings("UnusedDeclaration")
    public void setTotal( final int total )
    {
        this.total = total;
    }

    @SuppressWarnings("UnusedDeclaration")
    public boolean isDryRun()
    {
        return dryRun;
    }

    @SuppressWarnings("UnusedDeclaration")
    public List<String> getExportedNodes()
    {
        return exportedNodes;
    }

    @SuppressWarnings("UnusedDeclaration")
    public List<String> getExportErrors()
    {
        return exportErrors;
    }

    @SuppressWarnings("UnusedDeclaration")
    public List<String> getExportedBinaries()
    {
        return exportedBinaries;
    }
}
