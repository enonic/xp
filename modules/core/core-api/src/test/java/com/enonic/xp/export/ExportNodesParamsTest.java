package com.enonic.xp.export;

import org.junit.Test;

import com.enonic.xp.node.NodePath;

import static org.junit.Assert.*;

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
}
