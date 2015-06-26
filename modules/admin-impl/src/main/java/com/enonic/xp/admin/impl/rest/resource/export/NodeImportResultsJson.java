package com.enonic.xp.admin.impl.rest.resource.export;

import java.util.List;

import com.google.common.collect.Lists;

import com.enonic.xp.export.NodeImportResult;

public class NodeImportResultsJson
{
    private final List<NodeImportResultJson> nodeImportResults = Lists.newArrayList();

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
