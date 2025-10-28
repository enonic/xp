package com.enonic.xp.lib.export;

import java.nio.file.Path;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.enonic.xp.export.ExportError;
import com.enonic.xp.export.ExportService;
import com.enonic.xp.export.NodeExportResult;
import com.enonic.xp.home.HomeDirSupport;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.testing.ScriptTestSupport;
import com.enonic.xp.util.BinaryReference;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ExportHandlerTest
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
        final NodeExportResult result = NodeExportResult.create()
            .addNodePath( new NodePath( "/content" ) )
            .addBinary( new NodePath( "/binaryPath" ), BinaryReference.from( "ref" ) )
            .addError( new ExportError( "some error" ) )
            .build();

        when( exportService.exportNodes( any() ) ).thenReturn( result );
        runScript( "/lib/xp/examples/export/exportNodes.js" );
    }
}
