package com.enonic.xp.repo.impl.dump.writer;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipMethod;

import com.google.common.base.Preconditions;

import com.enonic.xp.blob.BlobStore;
import com.enonic.xp.core.internal.FileNames;
import com.enonic.xp.repo.impl.dump.DefaultFilePaths;
import com.enonic.xp.repo.impl.dump.FilePaths;
import com.enonic.xp.repo.impl.dump.PathRef;
import com.enonic.xp.repo.impl.dump.blobstore.ZipDumpWriteBlobStore;

public class ZipDumpWriter
    extends AbstractDumpWriter
{
    private static final String ZIP_FILE_EXTENSION = ".zip";

    private final ZipArchiveOutputStream zipArchiveOutputStream;

    private final ZipDumpWriteBlobStore dumpBlobStore;

    private ZipDumpWriter( FilePaths filePaths, ZipDumpWriteBlobStore dumpBlobStore,
                           ZipArchiveOutputStream zipArchiveOutputStream )
    {
        super( filePaths, dumpBlobStore );
        this.zipArchiveOutputStream = zipArchiveOutputStream;
        this.dumpBlobStore = dumpBlobStore;
    }

    public static ZipDumpWriter create( final Path basePath, final String dumpName, final BlobStore sourceBlobStore )
    {
        Preconditions.checkArgument( FileNames.isSafeFileName( dumpName ) );
        try
        {
            final ZipArchiveOutputStream zipArchiveOutputStream = new ZipArchiveOutputStream(
                Files.newByteChannel( basePath.resolve( dumpName + ZIP_FILE_EXTENSION ), StandardOpenOption.CREATE,
                                      StandardOpenOption.WRITE, StandardOpenOption.READ, StandardOpenOption.TRUNCATE_EXISTING ) );
            final ZipDumpWriteBlobStore zipDumpWriteBlobStore =
                new ZipDumpWriteBlobStore( dumpName, zipArchiveOutputStream, sourceBlobStore );
            return new ZipDumpWriter( new DefaultFilePaths( PathRef.of( dumpName ) ), zipDumpWriteBlobStore, zipArchiveOutputStream );
        }
        catch ( IOException e )
        {
            throw new UncheckedIOException( e );
        }
    }

    @Override
    protected OutputStream openMetaFileStream( final PathRef metaFile )
        throws IOException
    {
        final ZipArchiveEntry archiveEntry = new ZipArchiveEntry( metaFile.asString() );
        archiveEntry.setMethod( ZipMethod.STORED.getCode() );
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
    public void close()
        throws IOException
    {
        dumpBlobStore.flush();
        zipArchiveOutputStream.close();
    }
}
