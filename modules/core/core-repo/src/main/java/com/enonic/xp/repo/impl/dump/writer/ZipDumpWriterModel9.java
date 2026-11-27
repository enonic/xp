package com.enonic.xp.repo.impl.dump.writer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Collection;

import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.jspecify.annotations.Nullable;

import com.google.common.base.Preconditions;
import com.google.common.io.ByteSource;

import com.enonic.xp.blob.BlobKey;
import com.enonic.xp.blob.BlobStore;
import com.enonic.xp.blob.Segment;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.core.internal.FileNames;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeVersionKey;
import com.enonic.xp.repo.impl.dump.PathRef;
import com.enonic.xp.repo.impl.dump.RepoDumpException;
import com.enonic.xp.repo.impl.dump.blobstore.BlobReference;
import com.enonic.xp.repo.impl.dump.blobstore.DumpBlobStoreUtils;
import com.enonic.xp.repo.impl.dump.blobstore.ZipDumpBlobStore;
import com.enonic.xp.repo.impl.dump.model.CommitDumpEntry;
import com.enonic.xp.repo.impl.dump.model.DumpMeta;
import com.enonic.xp.repo.impl.dump.model.VersionMeta;
import com.enonic.xp.repo.impl.dump.serializer.DumpSerializer;
import com.enonic.xp.repo.impl.dump.serializer.json.DumpMetaJsonSerializer;
import com.enonic.xp.repo.impl.dump.serializer.json.JsonDumpSerializer;
import com.enonic.xp.repo.impl.node.NodeConstants;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.repository.RepositorySegmentUtils;

import static java.util.Objects.requireNonNull;

public class ZipDumpWriterModel9
    implements DumpWriter
{
    private static final String ZIP_FILE_EXTENSION = ".zip";

    private static final String PARTIAL_FILE_EXTENSION = ".partial";

    private final DedupZipArchiveOutputStream zipArchiveOutputStream;

    private final @Nullable ZipDumpBlobStore store;

    private final PathRef basePath;

    private final DumpSerializer serializer;

    private final Path partialFile;

    private final Path targetFile;

    private PathRef currentMetaPath;

    private boolean completed;

    private boolean closed;

    private ZipDumpWriterModel9( final PathRef basePath, final DedupZipArchiveOutputStream zipArchiveOutputStream,
                                 final @Nullable ZipDumpBlobStore store, final Path partialFile, final Path targetFile )
    {
        this.basePath = basePath;
        this.zipArchiveOutputStream = zipArchiveOutputStream;
        this.store = store;
        this.serializer = new JsonDumpSerializer();
        this.partialFile = partialFile;
        this.targetFile = targetFile;
    }

    public static ZipDumpWriterModel9 create( final Path basePath, final String dumpName, final BlobStore sourceBlobStore )
    {
        return doCreate( basePath, dumpName, requireNonNull( sourceBlobStore ) );
    }

    public static ZipDumpWriterModel9 create( final Path basePath, final String dumpName )
    {
        return doCreate( basePath, dumpName, null );
    }

    private static ZipDumpWriterModel9 doCreate( final Path basePath, final String dumpName, final @Nullable BlobStore sourceBlobStore )
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
            final DedupZipArchiveOutputStream zipArchiveOutputStream = new DedupZipArchiveOutputStream(
                new ZipArchiveOutputStream( Files.newOutputStream( partialFile, StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE ) ) );

            final PathRef basePathInZip = PathRef.of();
            final ZipDumpBlobStore store =
                sourceBlobStore != null ? new ZipDumpBlobStore( basePathInZip, sourceBlobStore::getRecord, zipArchiveOutputStream ) : null;
            return new ZipDumpWriterModel9( basePathInZip, zipArchiveOutputStream, store, partialFile, targetFile );
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
    public void openVersionsMeta( final RepositoryId repositoryId )
    {
        this.currentMetaPath = basePath.resolve( "meta" ).resolve( repositoryId.toString() ).resolve( "_versions" );
    }

    @Override
    public void openCommitsMeta( final RepositoryId repositoryId )
    {
        this.currentMetaPath = basePath.resolve( "meta" ).resolve( repositoryId.toString() ).resolve( "_commits" );
    }

    @Override
    public void closeMeta()
    {
        this.currentMetaPath = null;
        if ( store != null )
        {
            try
            {
                store.flush();
            }
            catch ( IOException e )
            {
                throw new RepoDumpException( "Cannot flush blob store", e );
            }
        }
    }

    @Override
    public void writeDumpMetaData( final DumpMeta dumpMeta )
    {
        writeZipEntry( basePath.resolve( "dump.json" ).asString(), ByteSource.wrap( new DumpMetaJsonSerializer().serialize( dumpMeta ) ) );
    }

    @Override
    public VersionsStream openVersions( final NodeId nodeId )
    {
        final String entryPath = currentMetaPath.resolve( nodeId + ".jsonl" ).asString();
        return new VersionsStream()
        {
            private final ByteArrayOutputStream buffer = new ByteArrayOutputStream();

            @Override
            public void append( final VersionMeta version, final Collection<Branch> branches )
            {
                try
                {
                    if ( buffer.size() > 0 )
                    {
                        buffer.write( '\n' );
                    }
                    buffer.write( serializer.serialize( nodeId.toString(), version, branches ) );
                }
                catch ( IOException e )
                {
                    throw new UncheckedIOException( e );
                }
            }

            @Override
            public void close()
            {
                writeZipEntry( entryPath, ByteSource.wrap( buffer.toByteArray() ) );
            }
        };
    }

    @Override
    public void writeCommitEntry( final CommitDumpEntry commitDumpEntry )
    {
        writeZipEntry( currentMetaPath.resolve( commitDumpEntry.nodeCommitId() + ".json" ).asString(),
                       ByteSource.wrap( serializer.serialize( commitDumpEntry ) ) );
    }

    @Override
    public void writeRawEntry( final String entryName, final byte[] data )
    {
        writeZipEntry( currentMetaPath.resolve( entryName ).asString(), ByteSource.wrap( data ) );
    }

    @Override
    public BlobKey addBlobRecord( final Segment segment, final ByteSource data )
    {
        final BlobKey key = BlobKey.sha256( data );
        final BlobReference reference = new BlobReference( segment, key );
        final PathRef blobPath = DumpBlobStoreUtils.getBlobPathRef( basePath, reference );

        writeZipEntry( blobPath.asString(), data );

        return key;
    }

    @Override
    public void writeNodeVersionBlobs( final RepositoryId repositoryId, final NodeVersionKey nodeVersionKey )
    {
        addBlob( RepositorySegmentUtils.toSegment( repositoryId, NodeConstants.NODE_SEGMENT_LEVEL ), nodeVersionKey.getNodeBlobKey() );
        addBlob( RepositorySegmentUtils.toSegment( repositoryId, NodeConstants.INDEX_CONFIG_SEGMENT_LEVEL ),
                 nodeVersionKey.getIndexConfigBlobKey() );
        addBlob( RepositorySegmentUtils.toSegment( repositoryId, NodeConstants.ACCESS_CONTROL_SEGMENT_LEVEL ),
                 nodeVersionKey.getAccessControlBlobKey() );
    }

    @Override
    public void writeBinaryBlob( final RepositoryId repositoryId, final BlobKey blobKey )
    {
        addBlob( RepositorySegmentUtils.toSegment( repositoryId, NodeConstants.BINARY_SEGMENT_LEVEL ), blobKey );
    }

    @Override
    public void complete()
    {
        this.completed = true;
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

    private void addBlob( final Segment segment, final BlobKey blobKey )
    {
        requireNonNull( store, "Source blob store is required for writeNodeVersionBlobs/writeBinaryBlob" )
            .add( new BlobReference( segment, blobKey ) );
    }

    private void writeZipEntry( final String entryPath, final ByteSource data )
    {
        if ( zipArchiveOutputStream.deduplication.contains( entryPath ) )
        {
            return;
        }
        try
        {
            zipArchiveOutputStream.put( entryPath, data );
        }
        catch ( IOException e )
        {
            throw new RepoDumpException( "Could not write zip entry [" + entryPath + "]", e );
        }
    }
}
