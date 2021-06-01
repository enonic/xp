package com.enonic.xp.lib.export;

import com.enonic.xp.export.ExportError;
import com.enonic.xp.export.NodeExportResult;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.script.serializer.MapGenerator;
import com.enonic.xp.script.serializer.MapSerializable;

public class NodeExportResultMapper
    implements MapSerializable

{
    private final NodeExportResult nodeExportResult;

    public NodeExportResultMapper( final NodeExportResult nodeExportResult )
    {
        this.nodeExportResult = nodeExportResult;
    }

    @Override
    public void serialize( final MapGenerator gen )
    {
        gen.array( "exportedNodes" );
        nodeExportResult.getExportedNodes().stream().map( NodePath::toString ).forEach( gen::value );
        gen.end();

        gen.array( "exportedBinaries" );
        nodeExportResult.getExportedBinaries().forEach( gen::value );
        gen.end();

        gen.array( "exportErrors" );
        nodeExportResult.getExportErrors().stream().map( ExportError::toString ).forEach( gen::value );
        gen.end();
    }
}
