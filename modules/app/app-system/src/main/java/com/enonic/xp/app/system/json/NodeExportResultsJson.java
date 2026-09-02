package com.enonic.xp.app.system.json;

import java.util.ArrayList;
import java.util.List;

import com.enonic.xp.export.NodeExportResult;

public class NodeExportResultsJson
{
    private final List<NodeExportResultJson> nodeExportResults = new ArrayList<>();

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
