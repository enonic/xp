package com.enonic.xp.export;

import org.junit.jupiter.api.Test;

import com.enonic.xp.node.NodePath;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExportNodesParamsTest
{
    @Test
    void builder()
    {
        ExportNodesParams.Builder builder = ExportNodesParams.create();

        builder.sourceNodePath( NodePath.ROOT ).exportName( "target" );
        ExportNodesParams result = builder.build();

        assertTrue( result.getSourceNodePath().isRoot() );
        assertEquals( "target", result.getExportName() );
    }

    @Test
    void batchSize_default_is_1000()
    {
        ExportNodesParams result = ExportNodesParams.create().sourceNodePath( NodePath.ROOT ).exportName( "target" ).build();

        assertEquals( 1000, result.getBatchSize() );
    }

    @Test
    void batchSize_custom()
    {
        ExportNodesParams result =
            ExportNodesParams.create().sourceNodePath( NodePath.ROOT ).exportName( "target" ).batchSize( 50 ).build();

        assertEquals( 50, result.getBatchSize() );
    }

    @Test
    void batchSize_large()
    {
        ExportNodesParams result =
            ExportNodesParams.create().sourceNodePath( NodePath.ROOT ).exportName( "target" ).batchSize( 1000 ).build();

        assertEquals( 1000, result.getBatchSize() );
    }
}
