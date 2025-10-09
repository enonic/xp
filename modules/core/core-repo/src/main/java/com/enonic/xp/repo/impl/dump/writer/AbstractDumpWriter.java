package com.enonic.xp.repo.impl.dump.writer;

import java.io.IOException;
import java.io.OutputStream;
import java.util.function.Consumer;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.blob.BlobKey;
import com.enonic.xp.node.NodeVersionKey;
import com.enonic.xp.blob.Segment;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.repo.impl.dump.FilePaths;
import com.enonic.xp.repo.impl.dump.PathRef;
import com.enonic.xp.repo.impl.dump.RepoDumpException;
import com.enonic.xp.repo.impl.dump.blobstore.BlobReference;
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

public abstract class AbstractDumpWriter
    implements DumpWriter
{
    private static final Logger LOG = LoggerFactory.getLogger( FileDumpWriter.class );

    private final Consumer<BlobReference> dumpBlobStore;

    private final DumpSerializer serializer;

    protected final FilePaths filePaths;

    protected TarArchiveOutputStream tarOutputStream;

    protected AbstractDumpWriter( final FilePaths filePaths, final Consumer<BlobReference> dumpBlobStore )
    {
        this.dumpBlobStore = dumpBlobStore;
        this.serializer = new JsonDumpSerializer();
        this.filePaths = filePaths;
    }

    @Override
    public void writeDumpMetaData( final DumpMeta dumpMeta )
    {
        final PathRef dumpMetaFile = filePaths.metaDataFile();

        try (OutputStream outputStream = openMetaFileStream( dumpMetaFile ))
        {
            outputStream.write( new DumpMetaJsonSerializer().serialize( dumpMeta ) );
        }
        catch ( IOException e )
        {
            throw new RepoDumpException( "Cannot write dump-meta file", e );
        }
    }

    @Override
    public void openBranchMeta( final RepositoryId repositoryId, final Branch branch )
    {
        final PathRef branchMetaPath = filePaths.branchMetaPath( repositoryId, branch );
        openTarStream( branchMetaPath );
    }

    @Override
    public void openVersionsMeta( final RepositoryId repositoryId )
    {
        final PathRef versionMetaPath = filePaths.versionMetaPath( repositoryId );
        openTarStream( versionMetaPath );
    }

    @Override
    public void openCommitsMeta( final RepositoryId repositoryId )
    {
        final PathRef commitMetaPath = filePaths.commitMetaPath( repositoryId );
        openTarStream( commitMetaPath );
    }

    @Override
    public void closeMeta()
    {
        try
        {
            this.tarOutputStream.close();
            flush();
        }
        catch ( IOException e )
        {
            LOG.warn( "Cannot close stream", e );
        }
    }

    @Override
    public void writeBranchEntry( final BranchDumpEntry branchDumpEntry )
    {
        final byte[] serializedEntry = serializer.serialize( branchDumpEntry );
        final String entryName = branchDumpEntry.getNodeId() + ".json";
        storeTarEntry( serializedEntry, entryName );
    }

    @Override
    public void writeVersionsEntry( final VersionsDumpEntry versionsDumpEntry )
    {
        final byte[] serializedEntry = serializer.serialize( versionsDumpEntry );
        final String entryName = versionsDumpEntry.getNodeId() + ".json";
        storeTarEntry( serializedEntry, entryName );
    }

    @Override
    public void writeCommitEntry( final CommitDumpEntry commitDumpEntry )
    {
        final byte[] serializedEntry = serializer.serialize( commitDumpEntry );
        final String entryName = commitDumpEntry.getNodeCommitId() + ".json";
        storeTarEntry( serializedEntry, entryName );
    }

    @Override
    public void writeNodeVersionBlobs( final RepositoryId repositoryId, final NodeVersionKey nodeVersionKey )
    {
        addBlob( RepositorySegmentUtils.toSegment( repositoryId, NodeConstants.NODE_SEGMENT_LEVEL ), nodeVersionKey.getNodeBlobKey() );
        addBlob( RepositorySegmentUtils.toSegment( repositoryId, NodeConstants.INDEX_CONFIG_SEGMENT_LEVEL ), nodeVersionKey.getIndexConfigBlobKey() );
        addBlob( RepositorySegmentUtils.toSegment( repositoryId, NodeConstants.ACCESS_CONTROL_SEGMENT_LEVEL ), nodeVersionKey.getAccessControlBlobKey() );
    }

    @Override
    public void writeBinaryBlob( final RepositoryId repositoryId, final BlobKey blobKey )
    {
        addBlob( RepositorySegmentUtils.toSegment( repositoryId, NodeConstants.BINARY_SEGMENT_LEVEL ), blobKey );
    }

    private void addBlob( final Segment segment, final BlobKey blobKey )
    {
        dumpBlobStore.accept( new BlobReference( segment, blobKey ) );
    }

    public void flush()
        throws IOException
    {
    }

    @Override
    public void close()
        throws IOException
    {
    }

    protected abstract OutputStream openMetaFileStream( PathRef metaFile )
        throws IOException;

    private void openTarStream( final PathRef metaPath )
    {
        try
        {
            this.tarOutputStream = new TarArchiveOutputStream( new GZIPOutputStream( openMetaFileStream( metaPath ) ) );
            this.tarOutputStream.setLongFileMode( TarArchiveOutputStream.LONGFILE_POSIX );
        }
        catch ( Exception e )
        {
            throw new RepoDumpException( "Could not open meta-file", e );
        }
    }

    public void storeTarEntry( final byte[] serializedEntry, final String entryName )
    {
        try
        {
            final TarArchiveEntry entry = new TarArchiveEntry( entryName );
            entry.setSize( serializedEntry.length );
            tarOutputStream.putArchiveEntry( entry );
            tarOutputStream.write( serializedEntry );
            tarOutputStream.closeArchiveEntry();
        }
        catch ( IOException e )
        {
            throw new RepoDumpException( "Could not write dump-entry", e );
        }
    }
}
