package com.enonic.xp.repo.impl.dump.writer;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;

import com.google.common.base.Preconditions;

import com.enonic.xp.blob.BlobStore;
import com.enonic.xp.core.internal.FileNames;
import com.enonic.xp.repo.impl.dump.DefaultFilePaths;
import com.enonic.xp.repo.impl.dump.FilePaths;
import com.enonic.xp.repo.impl.dump.PathRef;
import com.enonic.xp.repo.impl.dump.RepoDumpException;
import com.enonic.xp.repo.impl.dump.blobstore.ZipDumpBlobStore;
import com.enonic.xp.repo.impl.dump.model.DumpMeta;

public class ZipDumpWriter
    extends AbstractDumpWriter
{
    private static final String ZIP_FILE_EXTENSION = ".zip";

    private static final String PARTIAL_FILE_EXTENSION = ".partial";

    private final ZipArchiveOutputStream zipArchiveOutputStream;

    private final ZipDumpBlobStore store;

    private final Path partialFile;

    private final Path targetFile;

    private boolean completed;

    private boolean closed;

    private ZipDumpWriter( final FilePaths filePaths, final ZipArchiveOutputStream zipArchiveOutputStream,
                           final ZipDumpBlobStore store, final Path partialFile, final Path targetFile )
    {
        super( filePaths, store::add );
        this.zipArchiveOutputStream = zipArchiveOutputStream;
        this.store = store;
        this.partialFile = partialFile;
        this.targetFile = targetFile;
    }

    public static ZipDumpWriter create( final Path basePath, final String dumpName, final BlobStore sourceBlobStore )
    {
        Preconditions.checkArgument( FileNames.isSafeFileName( dumpName ) );
        final Path targetFile = basePath.resolve( dumpName + ZIP_FILE_EXTENSION );
        final Path partialFile = basePath.resolve( dumpName + ZIP_FILE_EXTENSION + PARTIAL_FILE_EXTENSION );

        if ( Files.exists( targetFile ) )
        {
            throw new RepoDumpException( "Dump '" + dumpName + "' already exists" );
        }

        try
        {
            final ZipArchiveOutputStream zipArchiveOutputStream = new ZipArchiveOutputStream(
                Files.newOutputStream( partialFile, StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE ) );

            final PathRef basePathInZip = PathRef.of( dumpName );
            return new ZipDumpWriter( new DefaultFilePaths( basePathInZip ), zipArchiveOutputStream,
                                      new ZipDumpBlobStore( basePathInZip, sourceBlobStore, zipArchiveOutputStream ), partialFile,
                                      targetFile );
        }
        catch ( FileAlreadyExistsException e )
        {
            throw new RepoDumpException( "Dump '" + dumpName + "' is already in progress or a previous one was interrupted: " +
                                             partialFile.getFileName() + " exists", e );
        }
        catch ( IOException e )
        {
            throw new UncheckedIOException( e );
        }
    }

    @Override
    public void writeDumpMetaData( final DumpMeta dumpMeta )
    {
        super.writeDumpMetaData( dumpMeta );
        // dump meta-data is written last and marks the dump as complete
        this.completed = true;
    }

    @Override
    protected OutputStream openMetaFileStream( final PathRef metaFile )
        throws IOException
    {
        final ZipArchiveEntry archiveEntry = new ZipArchiveEntry( metaFile.asString() );
        zipArchiveOutputStream.putArchiveEntry( archiveEntry );

        return new FilterOutputStream( zipArchiveOutputStream )
        {
            @Override
            public void close()
                throws IOException
            {
                zipArchiveOutputStream.closeArchiveEntry();
            }
        };
    }

    @Override
    public void flush()
        throws IOException
    {
        store.flush();
    }

    @Override
    public void close()
        throws IOException
    {
        if ( closed )
        {
            return;
        }
        closed = true;

        try
        {
            zipArchiveOutputStream.close();
            if ( completed )
            {
                Files.move( partialFile, targetFile );
            }
            else
            {
                Files.deleteIfExists( partialFile );
            }
        }
        catch ( IOException | RuntimeException e )
        {
            try
            {
                Files.deleteIfExists( partialFile );
            }
            catch ( IOException suppressed )
            {
                e.addSuppressed( suppressed );
            }
            throw e;
        }
    }
}
