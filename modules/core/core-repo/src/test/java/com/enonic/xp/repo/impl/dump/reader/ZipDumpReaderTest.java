package com.enonic.xp.repo.impl.dump.reader;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.repo.impl.dump.RepoLoadException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ZipDumpReaderTest
    extends BaseDumpReaderTest
{

    @BeforeEach
    void setUp()
        throws Exception
    {
        this.dumpFolder = Files.createDirectory( temporaryFolder.resolve( "myDump" ) );
    }

    @Test
    void test_archive_without_root_folder()
        throws Exception
    {
        createMetaDataFile( dumpFolder );

        final Path metaPath = createFolder( dumpFolder, "meta" );

        final Path repository = createFolder( metaPath, "repository" );
        createFolder( repository, "master" );
        createFolder( repository, "draft" );

        createZipArchive( dumpFolder, dumpFolder.getParent().resolve( "archive.zip" ) );

        try (ZipDumpReader zipDumpReader = ZipDumpReader.create( null, dumpFolder.getParent(), "archive" ))
        {
            assertNotNull( zipDumpReader.getDumpMeta() );
        }
    }

    @Test
    void test_archive_without_dumpJson()
        throws Exception
    {
        final Path metaPath = createFolder( dumpFolder, "meta" );

        final Path repository = createFolder( metaPath, "repository" );
        createFolder( repository, "master" );

        createZipArchive( dumpFolder, dumpFolder.getParent().resolve( "archive.zip" ) );

        final RepoLoadException exception = assertThrows( RepoLoadException.class, () -> {
            try (ZipDumpReader zipDumpReader = ZipDumpReader.create( null, dumpFolder.getParent(), "archive" ))
            {
                // do nothing
            }
        } );

        assertEquals( "Archive is not a valid dump archive: [archive]", exception.getMessage() );
    }

    @Test
    void test_archive_with_rootFolder_with_same_name()
        throws Exception
    {
        final Path rootPath = createFolder( dumpFolder, "archive" );
        createMetaDataFile( rootPath );

        createZipArchive( dumpFolder, dumpFolder.getParent().resolve( "archive.zip" ) );

        try (ZipDumpReader zipDumpReader = ZipDumpReader.create( null, dumpFolder.getParent(), "archive" ))
        {
            assertNotNull( zipDumpReader.getDumpMeta() );
        }
    }

    @Test
    void test_archive_with_dumpJson_not_in_root()
        throws Exception
    {
        final Path folder = createFolder( dumpFolder, "folder" );
        createMetaDataFile( folder );

        createZipArchive( dumpFolder, dumpFolder.getParent().resolve( "archive.zip" ) );

        try (ZipDumpReader zipDumpReader = ZipDumpReader.create( null, dumpFolder.getParent(), "archive" ))
        {
            assertNotNull( zipDumpReader.getDumpMeta() );
        }
    }

    void createZipArchive( final Path sourceDirPath, final Path zipFilePath )
        throws IOException
    {
        final Path archivePath = Files.createFile( zipFilePath );

        try (ZipOutputStream outputStream = new ZipOutputStream( Files.newOutputStream( archivePath ) ))
        {
            Files.walk( sourceDirPath ).
                forEach( path -> {
                    try
                    {
                        ZipEntry zipEntry;
                        if ( Files.isDirectory( path ) )
                        {
                            zipEntry = new ZipEntry( sourceDirPath.relativize( path ) + "/" );
                            outputStream.putNextEntry( zipEntry );
                        }
                        else
                        {
                            zipEntry = new ZipEntry( sourceDirPath.relativize( path ).toString() );
                            outputStream.putNextEntry( zipEntry );
                            Files.copy( path, outputStream );
                        }
                        outputStream.closeEntry();
                    }
                    catch ( IOException e )
                    {
                        throw new UncheckedIOException( e );
                    }
                } );
        }
    }

}
