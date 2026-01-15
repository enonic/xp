package com.enonic.xp.export;

import org.junit.jupiter.api.Test;

import com.enonic.xp.node.NodePath;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ExportNodesParamsTest
{
    @Test
    public void builder()
    {
        ExportNodesParams.Builder builder = ExportNodesParams.create();

        builder.dryRun( true ).includeNodeIds( true ).sourceNodePath( NodePath.ROOT ).targetDirectory( "target" );
        ExportNodesParams result = builder.build();

        assertTrue( result.isDryRun() );
        assertTrue( result.isIncludeNodeIds() );
        assertTrue( result.getSourceNodePath().isRoot() );
        assertEquals( "target", result.getRootDirectory() );
        assertEquals( "target", result.getTargetDirectory() );

        builder.rootDirectory( "root" );
        result = builder.build();

        assertEquals( "root", result.getRootDirectory() );
    }

    @Test
    public void batchSize_default()
    {
        ExportNodesParams result = ExportNodesParams.create().sourceNodePath( NodePath.ROOT ).targetDirectory( "target" ).build();

        assertEquals( 100, result.getBatchSize() );
    }

    @Test
    public void batchSize_custom()
    {
        ExportNodesParams result =
            ExportNodesParams.create().sourceNodePath( NodePath.ROOT ).targetDirectory( "target" ).batchSize( 50 ).build();

        assertEquals( 50, result.getBatchSize() );
    }

    @Test
    public void batchSize_large()
    {
        ExportNodesParams result =
            ExportNodesParams.create().sourceNodePath( NodePath.ROOT ).targetDirectory( "target" ).batchSize( 1000 ).build();

        assertEquals( 1000, result.getBatchSize() );
    }
}
