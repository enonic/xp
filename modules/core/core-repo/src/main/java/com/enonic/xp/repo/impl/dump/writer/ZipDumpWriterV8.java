package com.enonic.xp.repo.impl.dump.writer;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Objects;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.jspecify.annotations.Nullable;

import com.google.common.base.Preconditions;
import com.google.common.io.ByteSource;

import com.enonic.xp.blob.BlobKey;
import com.enonic.xp.blob.BlobStore;
import com.enonic.xp.blob.Segment;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.core.internal.FileNames;
import com.enonic.xp.node.NodeVersionKey;
import com.enonic.xp.repo.impl.dump.PathRef;
import com.enonic.xp.repo.impl.dump.RepoDumpException;
import com.enonic.xp.repo.impl.dump.blobstore.BlobReference;
import com.enonic.xp.repo.impl.dump.blobstore.DumpBlobStoreUtils;
import com.enonic.xp.repo.impl.dump.blobstore.ZipDumpBlobStore;
import com.enonic.xp.repo.impl.dump.model.BranchDumpEntry;
import com.enonic.xp.repo.impl.dump.model.CommitDumpEntry;
import com.enonic.xp.repo.impl.dump.model.DumpMeta;
import com.enonic.xp.repo.impl.dump.model.VersionsDumpEntry;
import com.enonic.xp.repo.impl.dump.serializer.DumpSerializer;
import com.enonic.xp.repo.impl.dump.serializer.json.DumpMetaJsonSerializer;
import com.enonic.xp.repo.impl.dump.serializer.json.JsonDumpSerializer;
import com.enonic.xp.repo.impl.node.NodeConstants;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.repository.RepositorySegmentUtils;

public class ZipDumpWriterV8
    implements DumpWriter
{
    private static final String ZIP_FILE_EXTENSION = ".zip";

    private final ZipArchiveOutputStream zipArchiveOutputStream;

    private final @Nullable ZipDumpBlobStore store;

    private final PathRef basePath;

    private final DumpSerializer serializer;

    private PathRef currentMetaPath;

    private ZipDumpWriterV8( final PathRef basePath, final ZipArchiveOutputStream zipArchiveOutputStream,
                             final @Nullable ZipDumpBlobStore store )
    {
        this.basePath = basePath;
        this.zipArchiveOutputStream = zipArchiveOutputStream;
        this.store = store;
        this.serializer = new JsonDumpSerializer();
    }

    public static ZipDumpWriterV8 create( final Path basePath, final String dumpName, final BlobStore sourceBlobStore )
    {
        Preconditions.checkArgument( FileNames.isSafeFileName( dumpName ) );
        try
        {
            final ZipArchiveOutputStream zipArchiveOutputStream = newZipOutputStream( basePath, dumpName );

            final PathRef basePathInZip = PathRef.of( dumpName );
            return new ZipDumpWriterV8( basePathInZip, zipArchiveOutputStream,
                                        new ZipDumpBlobStore( basePathInZip, sourceBlobStore, zipArchiveOutputStream ) );
        }
        catch ( IOException e )
        {
            throw new UncheckedIOException( e );
        }
    }

    public static ZipDumpWriterV8 create( final Path basePath, final String dumpName )
    {
        Preconditions.checkArgument( FileNames.isSafeFileName( dumpName ) );
        try
        {
            final ZipArchiveOutputStream zipArchiveOutputStream = newZipOutputStream( basePath, dumpName );

            final PathRef basePathInZip = PathRef.of( dumpName );
            return new ZipDumpWriterV8( basePathInZip, zipArchiveOutputStream, null );
        }
        catch ( IOException e )
        {
            throw new UncheckedIOException( e );
        }
    }

    private static ZipArchiveOutputStream newZipOutputStream( final Path basePath, final String dumpName )
        throws IOException
    {
        return new ZipArchiveOutputStream(
            Files.newByteChannel( basePath.resolve( dumpName + ZIP_FILE_EXTENSION ), StandardOpenOption.CREATE, StandardOpenOption.WRITE,
                                  StandardOpenOption.READ, StandardOpenOption.TRUNCATE_EXISTING ) );
    }

    @Override
    public void writeDumpMetaData( final DumpMeta dumpMeta )
    {
        final PathRef dumpMetaFile = basePath.resolve( "dump.json" );
        final byte[] data = new DumpMetaJsonSerializer().serialize( dumpMeta );
        writeZipEntry( dumpMetaFile.asString(), data );
    }

    @Override
    public void openBranchMeta( final RepositoryId repositoryId, final Branch branch )
    {
        this.currentMetaPath = basePath.resolve( "meta" ).resolve( repositoryId.toString() ).resolve( branch.toString() );
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
    public void writeBranchEntry( final BranchDumpEntry branchDumpEntry )
    {
        final byte[] serializedEntry = serializer.serialize( branchDumpEntry );
        final String entryName = branchDumpEntry.nodeId() + ".json";
        writeZipEntry( currentMetaPath.resolve( entryName ).asString(), serializedEntry );
    }

    @Override
    public void writeVersionsEntry( final VersionsDumpEntry versionsDumpEntry )
    {
        final byte[] serializedEntry = serializer.serialize( versionsDumpEntry );
        final String entryName = versionsDumpEntry.nodeId() + ".json";
        writeZipEntry( currentMetaPath.resolve( entryName ).asString(), serializedEntry );
    }

    @Override
    public void writeCommitEntry( final CommitDumpEntry commitDumpEntry )
    {
        final byte[] serializedEntry = serializer.serialize( commitDumpEntry );
        final String entryName = commitDumpEntry.nodeCommitId() + ".json";
        writeZipEntry( currentMetaPath.resolve( entryName ).asString(), serializedEntry );
    }

    @Override
    public void writeRawMetaEntry( final byte[] data, final String entryName )
    {
        writeZipEntry( currentMetaPath.resolve( entryName ).asString(), data );
    }

    @Override
    public BlobKey addBlobRecord( final Segment segment, final ByteSource data )
    {
        final BlobKey key = BlobKey.sha256( data );
        final BlobReference reference = new BlobReference( segment, key );
        final PathRef blobPath = DumpBlobStoreUtils.getBlobPathRef( basePath, reference );
        try
        {
            writeZipEntry( blobPath.asString(), data.read() );
        }
        catch ( IOException e )
        {
            throw new RepoDumpException( "Failed to write blob record", e );
        }
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
    public void close()
        throws IOException
    {
        zipArchiveOutputStream.close();
    }

    private void addBlob( final Segment segment, final BlobKey blobKey )
    {
        Objects.requireNonNull( store, "Source blob store is required for writeNodeVersionBlobs/writeBinaryBlob" )
            .add( new BlobReference( segment, blobKey ) );
    }

    private void writeZipEntry( final String entryPath, final byte[] data )
    {
        try
        {
            final ZipArchiveEntry zipEntry = new ZipArchiveEntry( entryPath );
            zipEntry.setSize( data.length );
            zipArchiveOutputStream.putArchiveEntry( zipEntry );
            zipArchiveOutputStream.write( data );
            zipArchiveOutputStream.closeArchiveEntry();
        }
        catch ( IOException e )
        {
            throw new RepoDumpException( "Could not write zip entry [" + entryPath + "]", e );
        }
    }
}
