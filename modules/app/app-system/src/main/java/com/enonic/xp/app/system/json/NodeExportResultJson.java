package com.enonic.xp.app.system.json;

import java.util.List;

import com.enonic.xp.export.ExportError;
import com.enonic.xp.export.NodeExportResult;
import com.enonic.xp.node.NodePath;

public record NodeExportResultJson(List<String> exportedNodes, List<String> exportErrors, List<String> exportedBinaries)
{
    public static NodeExportResultJson from( final NodeExportResult result )
    {
        return new NodeExportResultJson( result.getExportedNodes().stream().map( NodePath::toString ).toList(),
                                         result.getExportErrors().stream().map( ExportError::toString ).toList(),
                                         result.getExportedBinaries() );
    }

    @Override
    public String toString()
    {
        return JsonHelper.toJson( this );
    }
}
