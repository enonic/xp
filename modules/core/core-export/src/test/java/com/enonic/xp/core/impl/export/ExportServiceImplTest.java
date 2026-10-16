package com.enonic.xp.core.impl.export;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.enonic.xp.export.ExportNodeException;
import com.enonic.xp.export.ExportNodesParams;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ExportServiceImplTest
{
    @TempDir
    Path tempDir;

    private Path exportsDir;

    private ExportServiceImpl exportService;

    @BeforeEach
    void setUp()
    {
        exportsDir = tempDir.resolve( "exports" );

        final ExportConfiguration config = mock( ExportConfiguration.class );
        when( config.getExportsDir() ).thenReturn( exportsDir );

        exportService = new ExportServiceImpl( config, mock( NodeService.class ) );
    }

    @Test
    void exportNodesToArchive_publishesZipWhenComplete()
    {
        exportService.exportNodes(
            ExportNodesParams.create().exportName( "new-export" ).sourceNodePath( NodePath.ROOT ).archive( true ).build() );

        assertThat( exportsDir.resolve( "new-export.zip" ) ).exists();
        assertThat( exportsDir.resolve( "new-export.zip.partial" ) ).doesNotExist();
    }

    @Test
    void exportNodesToArchive_failsIfExportExists()
        throws IOException
    {
        Files.createDirectories( exportsDir );
        final Path existing = Files.writeString( exportsDir.resolve( "my-export.zip" ), "old" );

        assertThrows( ExportNodeException.class, () -> exportService.exportNodes(
            ExportNodesParams.create().exportName( "my-export" ).sourceNodePath( NodePath.ROOT ).archive( true ).build() ) );
        assertThat( existing ).hasContent( "old" );
    }
}
