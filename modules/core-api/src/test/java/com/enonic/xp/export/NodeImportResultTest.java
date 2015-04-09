package com.enonic.xp.export;

import org.junit.Test;

import com.enonic.xp.node.NodePath;
import com.enonic.xp.util.BinaryReference;

import static org.junit.Assert.*;

public class NodeImportResultTest
{
    @Test
    public void builder()
    {
        NodeImportResult.Builder builder = NodeImportResult.create();

        builder.dryRun( true ).addBinary( "path", BinaryReference.from( "binary ref" ) ).added( NodePath.ROOT ).updated(
            NodePath.ROOT ).addError( new ImportNodeException( "exception" ) ).addError( "ome more error",
                                                                                         new ImportNodeException( "exception",
                                                                                                                  new Exception() ) ).addError(
            new ImportNodeException( new Exception() ) );

        NodeImportResult result = builder.build();

        assertTrue( result.isDryRun() );
        assertEquals( 1, result.getExportedBinaries().size() );
        assertEquals( 1, result.getAddedNodes().getSize() );
        assertEquals( 1, result.getUpdateNodes().getSize() );
        assertEquals( 3, result.getImportErrors().size() );
    }

    @Test
    public void tostring()
    {
        NodeImportResult.Builder builder = NodeImportResult.create();

        builder.dryRun( true ).addBinary( "path", BinaryReference.from( "binary ref" ) ).added( NodePath.ROOT ).updated(
            NodePath.ROOT ).addError( new Exception() );

        NodeImportResult result = builder.build();

        String expected = "";
        expected += "NodeImportResult{";
        expected += "dryRun=true, ";
        expected += "addedNodes=[/], ";
        expected += "updateNodes=[/], ";
        expected += "importErrors=[ImportError{exception=java.lang.Exception, message='null'}], ";
        expected += "exportedBinaries=[path [binary ref]]";
        expected += "}";

        assertEquals( expected, result.toString() );
    }
}
