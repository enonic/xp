package com.enonic.xp.lib.export;


import java.nio.file.Path;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentCaptor;

import com.enonic.xp.export.ExportService;
import com.enonic.xp.export.ImportNodesParams;
import com.enonic.xp.export.NodeImportResult;
import com.enonic.xp.home.HomeDirSupport;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.testing.ScriptTestSupport;
import com.enonic.xp.util.BinaryReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ImportHandlerTest
    extends ScriptTestSupport
{
    @TempDir
    public static Path temporaryFolder;

    ExportService exportService;

    @BeforeAll
    public static void beforeAll()
    {
        HomeDirSupport.set( temporaryFolder );
    }


    public void initialize()
        throws Exception
    {
        super.initialize();
        this.exportService = mock( ExportService.class );
        addService( ExportService.class, exportService );
    }

    @Test
    void testExample()
    {
        final NodeImportResult result = NodeImportResult.create()
            .updated( new NodePath( "/updated" ) )
            .added( new NodePath( "/added" ) )
            .addBinary( "binaryPath", BinaryReference.from( "ref" ) )
            .addError( "error", new NoStacktraceException() )
            .build();

        when( exportService.importNodes( any() ) ).thenReturn( result );
        runScript( "/lib/xp/examples/export/importNodes.js" );
    }

    @Test
    void testImportWithoutXslt()
    {
        final NodeImportResult result = NodeImportResult.create().added( new NodePath( "/added" ) ).build();

        final ArgumentCaptor<ImportNodesParams> paramsCaptor = ArgumentCaptor.forClass( ImportNodesParams.class );
        when( exportService.importNodes( paramsCaptor.capture() ) ).thenReturn( result );

        runScript( "/lib/xp/examples/export/importNodesWithoutXslt.js" );

        final ImportNodesParams capturedParams = paramsCaptor.getValue();
        assertEquals( "myexport", capturedParams.getExportName() );
        assertEquals( new NodePath( "/content" ), capturedParams.getTargetNodePath() );
        assertFalse( capturedParams.isImportNodeIds() );
        assertFalse( capturedParams.isImportPermissions() );
    }

    private static class NoStacktraceException
        extends RuntimeException
    {
        NoStacktraceException()
        {
            super( null, null, false, false );
        }
    }

}
