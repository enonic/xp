package com.enonic.xp.app.system.json;

import java.util.List;

import com.enonic.xp.export.NodeExportResult;

public record NodeExportResultsJson(List<NodeExportResultJson> nodeExportResults)
{
    public static NodeExportResultsJson from( final List<NodeExportResult> results )
    {
        return new NodeExportResultsJson( results.stream().map( NodeExportResultJson::from ).toList() );
    }
}
