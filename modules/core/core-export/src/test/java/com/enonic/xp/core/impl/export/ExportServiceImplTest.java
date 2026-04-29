package com.enonic.xp.core.impl.export;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.enonic.xp.export.ExportInfo;
import com.enonic.xp.export.ListExportsResult;
import com.enonic.xp.node.NodeService;

import static org.assertj.core.api.Assertions.assertThat;
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

        final ExportConfigurationDynamic config = mock( ExportConfigurationDynamic.class );
        when( config.getExportsDir() ).thenReturn( exportsDir );

        exportService = new ExportServiceImpl( config, mock( NodeService.class ) );
    }

    @Test
    void list_returnsEmpty_whenExportsDirDoesNotExist()
    {
        final ListExportsResult result = exportService.list();

        assertThat( result.isEmpty() ).isTrue();
        assertThat( result.getSize() ).isZero();
    }

    @Test
    void list_returnsEmpty_whenExportsDirIsEmpty()
        throws IOException
    {
        Files.createDirectories( exportsDir );

        assertThat( exportService.list().isEmpty() ).isTrue();
    }

    @Test
    void list_stripsZipExtensionFromNames()
        throws IOException
    {
        Files.createDirectories( exportsDir );
        Files.createFile( exportsDir.resolve( "site-backup.zip" ) );

        assertThat( exportService.list().getList() ).extracting( ExportInfo::name ).containsExactly( "site-backup" );
    }

    @Test
    void list_ignoresNonZipFiles()
        throws IOException
    {
        Files.createDirectories( exportsDir );
        Files.createFile( exportsDir.resolve( "real.zip" ) );
        Files.createFile( exportsDir.resolve( "notes.txt" ) );
        Files.createFile( exportsDir.resolve( "archive.tar" ) );

        assertThat( exportService.list().getList() ).extracting( ExportInfo::name ).containsExactly( "real" );
    }

    @Test
    void list_ignoresSubdirectories()
        throws IOException
    {
        Files.createDirectories( exportsDir );
        Files.createDirectories( exportsDir.resolve( "nested.zip" ) );
        Files.createFile( exportsDir.resolve( "actual.zip" ) );

        assertThat( exportService.list().getList() ).extracting( ExportInfo::name ).containsExactly( "actual" );
    }

    @Test
    void list_ignoresHiddenFiles()
        throws IOException
    {
        Files.createDirectories( exportsDir );
        Files.createFile( exportsDir.resolve( ".hidden.zip" ) );
        Files.createFile( exportsDir.resolve( "visible.zip" ) );

        assertThat( exportService.list().getList() ).extracting( ExportInfo::name ).containsExactly( "visible" );
    }

    @Test
    void list_resultIsIterable()
        throws IOException
    {
        Files.createDirectories( exportsDir );
        Files.createFile( exportsDir.resolve( "one.zip" ) );
        Files.createFile( exportsDir.resolve( "two.zip" ) );

        final long count = exportService.list().stream().count();
        assertThat( count ).isEqualTo( 2L );
    }
}
