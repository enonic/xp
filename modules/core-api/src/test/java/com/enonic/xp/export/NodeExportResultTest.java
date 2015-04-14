package com.enonic.xp.export;

import org.junit.Test;

import com.enonic.xp.node.NodePath;
import com.enonic.xp.util.BinaryReference;

import static org.junit.Assert.*;

public class NodeExportResultTest
{
    @Test
    public void builder()
    {
        NodeExportResult.Builder builder = NodeExportResult.create();

        builder.dryRun( true ).addBinary( NodePath.ROOT, BinaryReference.from( "test binary ref" ) ).addError(
            new ExportError( "export error occured" ) ).addNodePath( NodePath.ROOT );

        NodeExportResult result = builder.build();

        assertTrue( result.isDryRun() );
        assertEquals( 1, result.getExportedBinaries().size() );
        assertEquals( 1, result.getExportErrors().size() );
        assertEquals( 1, result.getExportedNodes().getSize() );
        System.out.println( result.toString() );
    }

    @Test
    public void tostring()
    {
        NodeExportResult.Builder builder = NodeExportResult.create();

        builder.dryRun( true ).addBinary( NodePath.ROOT, BinaryReference.from( "test binary ref" ) ).addError(
            new ExportError( "export error occured" ) ).addNodePath( NodePath.ROOT );

        NodeExportResult result = builder.build();

        String expected = "";
        expected += "NodeExportResult{";
        expected += "dryRun=true, ";
        expected += "exportedNodes=[/], ";
        expected += "exportErrors=[export error occured], ";
        expected += "exportedBinaries=[/[test binary ref]]";
        expected += "}";

        assertEquals( expected, result.toString() );
    }
}
