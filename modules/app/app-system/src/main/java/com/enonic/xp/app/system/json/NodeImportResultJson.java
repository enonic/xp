package com.enonic.xp.app.system.json;

import java.util.List;

import com.enonic.xp.export.NodeImportResult;
import com.enonic.xp.node.NodePath;

public record NodeImportResultJson(List<String> addedNodes, List<String> updateNodes, List<String> skippedNodes, List<String> importErrors,
                                   List<String> importedBinaries)
{
    public static NodeImportResultJson from( final NodeImportResult result )
    {
        return new NodeImportResultJson( result.getAddedNodes().stream().map( NodePath::toString ).toList(),
                                         result.getUpdateNodes().stream().map( NodePath::toString ).toList(),
                                         result.getSkippedNodes().stream().map( NodePath::toString ).toList(),
                                         result.getImportErrors().stream().map( e -> e.getMessage() + " - " + e.getException() ).toList(),
                                         List.copyOf( result.getImportedBinaries() ) );
    }

    @Override
    public String toString()
    {
        return JsonHelper.toJson( this );
    }
}
