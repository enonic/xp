package com.enonic.xp.core.impl.export;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.util.List;

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
    void list_sortsByCreationTime_newestFirst()
        throws IOException
    {
        Files.createDirectories( exportsDir );

        final Path oldest = Files.createFile( exportsDir.resolve( "alpha.zip" ) );
        final Path middle = Files.createFile( exportsDir.resolve( "beta.zip" ) );
        final Path newest = Files.createFile( exportsDir.resolve( "gamma.zip" ) );

        setCreationTime( oldest, Instant.parse( "2026-01-01T00:00:00Z" ) );
        setCreationTime( middle, Instant.parse( "2026-02-01T00:00:00Z" ) );
        setCreationTime( newest, Instant.parse( "2026-04-29T00:00:00Z" ) );

        final List<String> names = exportService.list().stream().map( ExportInfo::name ).toList();

        assertThat( names ).containsExactly( "gamma", "beta", "alpha" );
    }

    @Test
    void list_resultIsIterable()
        throws IOException
    {
        Files.createDirectories( exportsDir );
        Files.createFile( exportsDir.resolve( "one.zip" ) );
        Files.createFile( exportsDir.resolve( "two.zip" ) );

        int count = 0;
        for ( ExportInfo ignored : exportService.list() )
        {
            count++;
        }
        assertThat( count ).isEqualTo( 2 );
    }

    private static void setCreationTime( final Path path, final Instant instant )
        throws IOException
    {
        final FileTime fileTime = FileTime.from( instant );
        Files.getFileAttributeView( path, BasicFileAttributeView.class ).setTimes( fileTime, null, fileTime );
    }
}
