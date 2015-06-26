package com.enonic.xp.admin.impl.rest.resource.export;

import java.util.List;

import com.google.common.collect.Lists;

import com.enonic.xp.export.NodeExportResult;

public class NodeExportResultsJson
{
    private final List<NodeExportResultJson> nodeExportResults = Lists.newArrayList();

    public static NodeExportResultsJson from( final List<NodeExportResult> results )
    {
        final NodeExportResultsJson nodeExportResultsJson = new NodeExportResultsJson();

        for ( final NodeExportResult result : results )
        {
            nodeExportResultsJson.nodeExportResults.add( NodeExportResultJson.from( result ) );
        }

        return nodeExportResultsJson;
    }

    @SuppressWarnings("UnusedDeclaration")
    public List<NodeExportResultJson> getNodeExportResults()
    {
        return nodeExportResults;
    }
}
