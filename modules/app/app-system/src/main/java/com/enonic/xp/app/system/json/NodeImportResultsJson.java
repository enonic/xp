package com.enonic.xp.app.system.json;

import java.util.ArrayList;
import java.util.List;

import com.enonic.xp.export.NodeImportResult;

public class NodeImportResultsJson
{
    private final List<NodeImportResultJson> nodeImportResults = new ArrayList<>();

    public static NodeImportResultsJson from( final List<NodeImportResult> results )
    {
        final NodeImportResultsJson nodeImportResultsJson = new NodeImportResultsJson();

        for ( final NodeImportResult result : results )
        {
            nodeImportResultsJson.nodeImportResults.add( NodeImportResultJson.from( result ) );
        }

        return nodeImportResultsJson;
    }

    @SuppressWarnings("UnusedDeclaration")
    public List<NodeImportResultJson> getNodeImportResults()
    {
        return nodeImportResults;
    }
}
