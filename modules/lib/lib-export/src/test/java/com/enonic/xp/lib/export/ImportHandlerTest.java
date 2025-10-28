package com.enonic.xp.lib.export;


import java.nio.file.Path;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.export.ExportService;
import com.enonic.xp.export.NodeImportResult;
import com.enonic.xp.home.HomeDirSupport;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.testing.ScriptTestSupport;
import com.enonic.xp.util.BinaryReference;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ImportHandlerTest
    extends ScriptTestSupport
{
    @TempDir
    public static Path temporaryFolder;

    ApplicationService applicationService;

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
        this.applicationService = mock( ApplicationService.class );
        addService( ApplicationService.class, applicationService );
        this.exportService = mock( ExportService.class );
        addService( ExportService.class, exportService );
    }

    @Test
    void testExample()
    {
        when( applicationService.getInstalledApplication( ApplicationKey.from( "myapplication" ) ) ).thenReturn(
            mock( Application.class ) );

        final NodeImportResult result = NodeImportResult.create()
            .updated( new NodePath( "/updated" ) )
            .added( new NodePath( "/added" ) )
            .addBinary( "binaryPath", BinaryReference.from( "ref" ) )
            .addError( "error", new NoStacktraceException() )
            .build();

        when( exportService.importNodes( any() ) ).thenReturn( result );
        runScript( "/lib/xp/examples/export/importNodes.js" );
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
