package com.enonic.xp.lib.export;

import java.util.Arrays;

import com.enonic.xp.export.NodeImportResult;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.script.serializer.MapGenerator;
import com.enonic.xp.script.serializer.MapSerializable;

public class NodeImportResultMapper
    implements MapSerializable
{
    private final NodeImportResult nodeImportResult;

    public NodeImportResultMapper( final NodeImportResult nodeImportResult )
    {
        this.nodeImportResult = nodeImportResult;
    }

    @Override
    public void serialize( final MapGenerator gen )
    {
        gen.array( "addedNodes" );
        nodeImportResult.getAddedNodes().stream().map( NodePath::toString ).forEach( gen::value );
        gen.end();

        gen.array( "updatedNodes" );
        nodeImportResult.getUpdateNodes().stream().map( NodePath::toString ).forEach( gen::value );
        gen.end();

        gen.array( "skippedNodes" );
        nodeImportResult.getSkippedNodes().stream().map( NodePath::toString ).forEach( gen::value );
        gen.end();

        gen.array( "importedBinaries" );
        nodeImportResult.getImportedBinaries().forEach( gen::value );
        gen.end();

        gen.array( "importErrors" );
        nodeImportResult.getImportErrors().forEach( importError -> {
            gen.map();
            gen.value( "exception", importError.getException() );
            gen.value( "message", importError.getMessage() );
            gen.array( "stacktrace" );
            Arrays.stream( importError.getStacktrace() ).map( StackTraceElement::toString ).forEach( gen::value );
            gen.end();
            gen.end();
        } );
        gen.end();
    }
}
