package com.enonic.xp.repo.impl.dump.writer;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipMethod;

import com.google.common.base.Preconditions;

import com.enonic.xp.blob.BlobRecord;
import com.enonic.xp.blob.BlobStore;
import com.enonic.xp.blob.BlobStoreException;
import com.enonic.xp.core.internal.FileNames;
import com.enonic.xp.repo.impl.dump.DefaultFilePaths;
import com.enonic.xp.repo.impl.dump.FilePaths;
import com.enonic.xp.repo.impl.dump.PathRef;
import com.enonic.xp.repo.impl.dump.blobstore.BlobReference;
import com.enonic.xp.repo.impl.dump.blobstore.DumpBlobStoreUtils;

public class ZipDumpWriter
    extends AbstractDumpWriter
{
    private static final String ZIP_FILE_EXTENSION = ".zip";

    private final ZipArchiveOutputStream zipArchiveOutputStream;

    private final Map<String, BlobReference> records;

    private final BlobStore sourceBlobStore;

    private ZipDumpWriter( final FilePaths filePaths, final BlobStore sourceBlobStore,
                           final ZipArchiveOutputStream zipArchiveOutputStream, final Map<String, BlobReference> records )
    {
        super( filePaths, reference -> records.put( DumpBlobStoreUtils.getBlobPathRef( filePaths.basePath(), reference ).asString(), reference ) );
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

            return new ZipDumpWriter( new DefaultFilePaths( PathRef.of( dumpName ) ), sourceBlobStore, zipArchiveOutputStream, new HashMap<>() );
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
        final List<BlobReference> notFound = new ArrayList<>();
        try
        {
            // It is not possible to write into multiple zip entries at the same time.
            // We delay writing the blobs until the very end, when writing of metadata zip-entry is already complete.
            for ( var entry : records.entrySet() )
            {
                final String zipEntryName = entry.getKey();
                final BlobReference reference = entry.getValue();

                final BlobRecord record = sourceBlobStore.getRecord( reference.getSegment(), reference.getKey() );
                if ( record == null )
                {
                    notFound.add( reference );
                    continue;
                }

                zipArchiveOutputStream.putArchiveEntry( new ZipArchiveEntry( zipEntryName ) );
                record.getBytes().copyTo( zipArchiveOutputStream );
                zipArchiveOutputStream.closeArchiveEntry();
            }
            if ( !notFound.isEmpty() )
            {
                throw new BlobStoreException( "Blobs not found: " + notFound );
            }
        }
        finally
        {
            zipArchiveOutputStream.close();
        }
    }
}
