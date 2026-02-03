package com.enonic.xp.export;

import org.junit.jupiter.api.Test;

import com.enonic.xp.node.NodePath;
import com.enonic.xp.util.BinaryReference;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NodeImportResultTest
{
    @Test
    void builder()
    {
        NodeImportResult.Builder builder = NodeImportResult.create();

        builder.addBinary( "path", BinaryReference.from( "binary ref" ) ).added( NodePath.ROOT ).updated(
            NodePath.ROOT ).addError( new ImportNodeException( "exception" ) ).addError( "ome more error",
                                                                                         new ImportNodeException( "exception",
                                                                                                                  new Exception() ) ).addError(
            new ImportNodeException( new Exception() ) );

        NodeImportResult result = builder.build();

        assertEquals( 1, result.getImportedBinaries().size() );
        assertEquals( 1, result.getAddedNodes().getSize() );
        assertEquals( 1, result.getUpdateNodes().getSize() );
        assertEquals( 3, result.getImportErrors().size() );
    }

    @Test
    void tostring()
    {
        NodeImportResult.Builder builder = NodeImportResult.create();

        builder.addBinary( "path", BinaryReference.from( "binary ref" ) ).added( NodePath.ROOT ).updated(
            NodePath.ROOT ).addError( new Exception() );

        NodeImportResult result = builder.build();

        String expected = "";
        expected += "NodeImportResult{";
        expected += " addedNodes=[/], ";
        expected += "updateNodes=[/], ";
        expected += "skippedNodes=[], ";
        expected += "importErrors=[ImportError{exception=java.lang.Exception, message='null'}], ";
        expected += "importedBinaries=[path [binary ref]]";
        expected += "}";

        assertEquals( expected, result.toString() );
    }
}
