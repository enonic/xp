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

        builder.includeNodeIds( true ).sourceNodePath( NodePath.ROOT ).exportName( "target" );
        ExportNodesParams result = builder.build();

        assertTrue( result.isIncludeNodeIds() );
        assertTrue( result.getSourceNodePath().isRoot() );
        assertEquals( "target", result.getExportName() );
    }
}
