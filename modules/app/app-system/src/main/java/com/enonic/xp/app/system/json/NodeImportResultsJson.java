package com.enonic.xp.app.system.json;

import java.util.List;

import com.enonic.xp.export.NodeImportResult;

public record NodeImportResultsJson(List<NodeImportResultJson> nodeImportResults)
{
    public static NodeImportResultsJson from( final List<NodeImportResult> results )
    {
        return new NodeImportResultsJson( results.stream().map( NodeImportResultJson::from ).toList() );
    }
}
