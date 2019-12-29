package com.enonic.xp.repo.impl.dump.writer;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.blob.BlobKey;
import com.enonic.xp.blob.BlobRecord;
import com.enonic.xp.blob.BlobStore;
import com.enonic.xp.blob.NodeVersionKey;
import com.enonic.xp.blob.Segment;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.repo.impl.dump.AbstractFileProcessor;
import com.enonic.xp.repo.impl.dump.DumpBlobStore;
import com.enonic.xp.repo.impl.dump.DumpConstants;
import com.enonic.xp.repo.impl.dump.RepoDumpException;
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

public class FileDumpWriter
    extends AbstractFileProcessor
    implements DumpWriter
{
    private final static Logger LOG = LoggerFactory.getLogger( FileDumpWriter.class );

    private final BlobStore dumpBlobStore;

    private final BlobStore blobStore;

    private final Path dumpDirectory;

    private final DumpSerializer serializer;

    private GZIPOutputStream gzipOut;

    private OutputStream fileOut;

    private TarArchiveOutputStream tarOutputStream;

    public FileDumpWriter( final Path basePath, final String dumpName, final BlobStore blobStore )
    {
        this.dumpDirectory = getDumpDirectory( basePath, dumpName );
        this.dumpBlobStore = new DumpBlobStore( this.dumpDirectory.toFile() );
        this.serializer = new JsonDumpSerializer();
        this.blobStore = blobStore;
    }

    private Path getDumpDirectory( final Path basePath, final String name )
    {
        return Paths.get( basePath.toString(), name ).toAbsolutePath();
    }

    @Override
    public void writeDumpMetaData( final DumpMeta dumpMeta )
    {
        final Path dumpMetaFile = Paths.get( this.dumpDirectory.toString(), "dump.json" );

        final String serialized = new DumpMetaJsonSerializer().serialize( dumpMeta );

        try
        {
            Files.writeString( dumpMetaFile, serialized );
        }
        catch ( IOException e )
        {
            throw new RepoDumpException( "Cannot write dump-meta file", e );
        }
    }

    @Override
    public void openBranchMeta( final RepositoryId repositoryId, final Branch branch )
    {
        try
        {
            final Path metaFile = createBranchMeta( repositoryId, branch );
            doOpenFile( metaFile );
        }
        catch ( Exception e )
        {
            throw new RepoDumpException( "Could not open meta-file", e );
        }
    }

    @Override
    public void openVersionsMeta( final RepositoryId repositoryId )
    {
        try
        {
            final Path metaFile = createVersionsMeta( repositoryId );
            doOpenFile( metaFile );
        }
        catch ( Exception e )
        {
            throw new RepoDumpException( "Could not open meta-file", e );
        }
    }

    @Override
    public void openCommitsMeta( final RepositoryId repositoryId )
    {
        try
        {
            final Path metaFile = createCommitsMeta( repositoryId );
            doOpenFile( metaFile );
        }
        catch ( Exception e )
        {
            throw new RepoDumpException( "Could not open meta-file", e );
        }
    }

    private void doOpenFile( final Path metaFile )
        throws IOException
    {
        this.fileOut = Files.newOutputStream( metaFile );
        this.gzipOut = new GZIPOutputStream( fileOut );
        this.tarOutputStream = new TarArchiveOutputStream( gzipOut );
        this.tarOutputStream.setLongFileMode( TarArchiveOutputStream.LONGFILE_POSIX );
    }

    private Path createBranchMeta( final RepositoryId repositoryId, final Branch branch )
    {
        final Path metaFile = createBranchMetaPath( this.dumpDirectory, repositoryId, branch );

        return doCreateMetaFile( metaFile );
    }

    private Path createVersionsMeta( final RepositoryId repositoryId )
    {
        final Path metaFile = createVersionMetaPath( this.dumpDirectory, repositoryId );

        return doCreateMetaFile( metaFile );
    }

    private Path createCommitsMeta( final RepositoryId repositoryId )
    {
        final Path metaFile = createCommitMetaPath( this.dumpDirectory, repositoryId );

        return doCreateMetaFile( metaFile );
    }

    private Path doCreateMetaFile( final Path metaFile )
    {
        if ( Files.exists( metaFile ) )
        {
            throw new RepoDumpException( "Meta-file with path [" + metaFile + "] already exists" );
        }

        try
        {
            Files.createDirectories( metaFile.getParent() );
        }
        catch ( IOException e )
        {
            throw new RepoDumpException( "Not able to create parent-directory [" + metaFile.getParent() + "]" );
        }

        return metaFile;
    }

    @Override
    public void writeBranchEntry( final BranchDumpEntry branchDumpEntry )
    {
        final String serializedEntry = serializer.serialize( branchDumpEntry );
        final String entryName = branchDumpEntry.getNodeId().toString() + ".json";
        storeTarEntry( serializedEntry, entryName );
    }


    @Override
    public void writeVersionsEntry( final VersionsDumpEntry versionsDumpEntry )
    {
        final String serializedEntry = serializer.serialize( versionsDumpEntry );
        final String entryName = versionsDumpEntry.getNodeId().toString() + ".json";
        storeTarEntry( serializedEntry, entryName );
    }

    @Override
    public void writeCommitEntry( final CommitDumpEntry commitDumpEntry )
    {
        final String serializedEntry = serializer.serialize( commitDumpEntry );
        final String entryName = commitDumpEntry.getNodeCommitId().toString() + ".json";
        storeTarEntry( serializedEntry, entryName );
    }

    public void storeTarEntry( final String serializedEntry, final String entryName )
    {
        try
        {
            final byte[] data = serializedEntry.getBytes( StandardCharsets.UTF_8 );
            final TarArchiveEntry entry = new TarArchiveEntry( entryName );
            entry.setSize( data.length );
            tarOutputStream.putArchiveEntry( entry );
            tarOutputStream.write( data );
            tarOutputStream.closeArchiveEntry();
        }
        catch ( IOException e )
        {
            throw new RepoDumpException( "Could not write dump-entry", e );
        }
    }

    @Override
    public void writeNodeVersionBlobs( final RepositoryId repositoryId, final NodeVersionKey nodeVersionKey )
    {
        final Segment nodeDumpSegment = RepositorySegmentUtils.toSegment( repositoryId, DumpConstants.DUMP_NODE_SEGMENT_LEVEL );
        final BlobRecord existingNodeBlobRecord = blobStore.getRecord( nodeDumpSegment, nodeVersionKey.getNodeBlobKey() );
        if ( existingNodeBlobRecord == null )
        {
            throw new RepoDumpException(
                "Cannot write node blob with key [" + nodeVersionKey.getNodeBlobKey() + "], not found in blobStore" );
        }

        final Segment indexConfigDumpSegment =
            RepositorySegmentUtils.toSegment( repositoryId, DumpConstants.DUMP_INDEX_CONFIG_SEGMENT_LEVEL );
        final BlobRecord existingIndexConfigBlobRecord =
            blobStore.getRecord( indexConfigDumpSegment, nodeVersionKey.getIndexConfigBlobKey() );
        if ( existingIndexConfigBlobRecord == null )
        {
            throw new RepoDumpException(
                "Cannot write index config blob with key [" + nodeVersionKey.getIndexConfigBlobKey() + "], not found in blobStore" );
        }

        final Segment accessControlDumpSegment =
            RepositorySegmentUtils.toSegment( repositoryId, DumpConstants.DUMP_ACCESS_CONTROL_SEGMENT_LEVEL );
        final BlobRecord existingAccessControlBlobRecord =
            blobStore.getRecord( accessControlDumpSegment, nodeVersionKey.getAccessControlBlobKey() );
        if ( existingAccessControlBlobRecord == null )
        {
            throw new RepoDumpException(
                "Cannot write access control blob with key [" + nodeVersionKey.getAccessControlBlobKey() + "], not found in blobStore" );
        }

        final Segment nodeSegment = RepositorySegmentUtils.toSegment( repositoryId, NodeConstants.NODE_SEGMENT_LEVEL );
        this.dumpBlobStore.addRecord( nodeSegment, existingNodeBlobRecord.getBytes() );

        final Segment indexConfigSegment = RepositorySegmentUtils.toSegment( repositoryId, NodeConstants.INDEX_CONFIG_SEGMENT_LEVEL );
        this.dumpBlobStore.addRecord( indexConfigSegment, existingIndexConfigBlobRecord.getBytes() );

        final Segment accessControlSegment = RepositorySegmentUtils.toSegment( repositoryId, NodeConstants.ACCESS_CONTROL_SEGMENT_LEVEL );
        this.dumpBlobStore.addRecord( accessControlSegment, existingAccessControlBlobRecord.getBytes() );
    }

    @Override
    public void writeBinaryBlob( final RepositoryId repositoryId, final String blobKey )
    {
        final Segment dumpSegment = RepositorySegmentUtils.toSegment( repositoryId, DumpConstants.DUMP_BINARY_SEGMENT_LEVEL );
        final BlobRecord binaryRecord = blobStore.getRecord( dumpSegment, BlobKey.from( blobKey ) );

        if ( binaryRecord == null )
        {
            throw new RepoDumpException( "Cannot write binary with key [" + blobKey + "], not found in blobStore" );
        }

        final Segment segment = RepositorySegmentUtils.toSegment( repositoryId, NodeConstants.BINARY_SEGMENT_LEVEL );
        this.dumpBlobStore.addRecord( segment, binaryRecord.getBytes() );
    }

    @Override
    public void close()
    {
        closeStream( this.tarOutputStream );
        closeStream( this.gzipOut );
        closeStream( this.fileOut );
    }

    private void closeStream( final OutputStream stream )
    {
        if ( stream == null )
        {
            return;
        }

        try
        {
            stream.flush();
            stream.close();
        }
        catch ( IOException e )
        {
            LOG.warn( "Cannot close stream [" + stream.getClass().getName() + "]", e );
        }
    }
}
