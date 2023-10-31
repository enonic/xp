package com.enonic.xp.repo.impl.dump.writer;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipMethod;

import com.google.common.base.Preconditions;

import com.enonic.xp.blob.BlobStore;
import com.enonic.xp.core.internal.FileNames;
import com.enonic.xp.repo.impl.dump.DefaultFilePaths;
import com.enonic.xp.repo.impl.dump.PathRef;
import com.enonic.xp.repo.impl.dump.blobstore.BlobReference;
import com.enonic.xp.repo.impl.dump.blobstore.DumpBlobStoreUtils;

public class ZipDumpWriter
    extends AbstractDumpWriter
{
    private static final String ZIP_FILE_EXTENSION = ".zip";

    private final ZipArchiveOutputStream zipArchiveOutputStream;

    private final Set<BlobReference> records;

    private final BlobStore sourceBlobStore;

    private ZipDumpWriter( final PathRef basePathInZip, final BlobStore sourceBlobStore,
                           final ZipArchiveOutputStream zipArchiveOutputStream, final Set<BlobReference> records )
    {
        super( new DefaultFilePaths( basePathInZip ), records::add );
        this.records = records;
        this.sourceBlobStore = sourceBlobStore;
        this.zipArchiveOutputStream = zipArchiveOutputStream;
    }

    public static ZipDumpWriter create( final Path basePath, final String dumpName, final BlobStore sourceBlobStore )
    {
        Preconditions.checkArgument( FileNames.isSafeFileName( dumpName ) );
        try
        {
            final ZipArchiveOutputStream zipArchiveOutputStream = new ZipArchiveOutputStream(
                Files.newByteChannel( basePath.resolve( dumpName + ZIP_FILE_EXTENSION ), StandardOpenOption.CREATE,
                                      StandardOpenOption.WRITE, StandardOpenOption.READ, StandardOpenOption.TRUNCATE_EXISTING ) );

            return new ZipDumpWriter( PathRef.of( dumpName ), sourceBlobStore, zipArchiveOutputStream, new HashSet<>() );
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

        try
        {
            // It is not possible to write into multiple zip entries at the same time.
            // We delay writing the blobs until the very end, when writing of metadata zip-entry is already complete.
            for ( BlobReference reference : records )
            {
                final String zipEntryName = DumpBlobStoreUtils.getBlobPathRef( filePaths.basePath(), reference ).asString();
                zipArchiveOutputStream.putArchiveEntry( new ZipArchiveEntry( zipEntryName ) );
                sourceBlobStore.getRecord( reference.getSegment(), reference.getKey() ).getBytes().copyTo( zipArchiveOutputStream );
                zipArchiveOutputStream.closeArchiveEntry();
            }
        }
        finally
        {
            zipArchiveOutputStream.close();
        }
    }
}
