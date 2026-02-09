package com.enonic.xp.repo.impl.dump.reader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.ByteSource;

import com.enonic.xp.blob.BlobKey;
import com.enonic.xp.blob.BlobStore;
import com.enonic.xp.blob.Segment;
import com.enonic.xp.node.AttachedBinary;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.repo.impl.NodeStoreVersion;
import com.enonic.xp.repo.impl.dump.RepoLoadException;
import com.enonic.xp.repo.impl.dump.model.VersionMeta;
import com.enonic.xp.repo.impl.dump.serializer.DumpSerializer;
import com.enonic.xp.repo.impl.dump.serializer.json.JsonDumpSerializer;
import com.enonic.xp.repo.impl.node.NodeConstants;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.repository.RepositorySegmentUtils;

class AbstractEntryProcessor
{
    private static final Logger LOG = LoggerFactory.getLogger( AbstractEntryProcessor.class );

    private final BlobStore blobStore;

    final NodeService nodeService;

    final DumpReader dumpReader;

    final DumpSerializer serializer;

    final RepositoryId repositoryId;

    AbstractEntryProcessor( final Builder<?> builder )
    {
        blobStore = builder.blobStore;
        nodeService = builder.nodeService;
        dumpReader = builder.dumpReader;
        serializer = new JsonDumpSerializer();
        repositoryId = builder.repositoryId;
    }

    void addBinary( final NodeStoreVersion nodeVersion, final EntryLoadResult.Builder result )
    {
        final Segment segment = RepositorySegmentUtils.toSegment( repositoryId, NodeConstants.BINARY_SEGMENT_LEVEL );
        nodeVersion.attachedBinaries().forEach( binary -> {
            try
            {
                final ByteSource dumpBinary = this.dumpReader.getBinary( repositoryId, BlobKey.from( binary.getBlobKey() ) );
                this.blobStore.addRecord( segment, dumpBinary );
            }
            catch ( RepoLoadException e )
            {
                reportBinaryError( nodeVersion, result, binary, e );
            }
        } );
    }

    private void reportBinaryError( final NodeStoreVersion nodeVersion, final EntryLoadResult.Builder result, final AttachedBinary binary,
                                    final RepoLoadException e )
    {
        final String message = String.format( "Failed to load binary for nodeId %s, blobKey %s", nodeVersion.id(), binary.getBlobKey() );
        result.error( EntryLoadError.error( message ) );
        LOG.error( message, e );
    }

    void reportVersionError( final EntryLoadResult.Builder result, final VersionMeta meta )
    {
        final String message =
            String.format( "Failed to load version for node with path %s, blobKey %s", meta.nodePath(), meta.version() );
        result.error( EntryLoadError.error( message ) );
        LOG.error( message );
    }

    public static class Builder<B extends Builder<?>>
    {
        private BlobStore blobStore;

        private NodeService nodeService;

        private DumpReader dumpReader;

        private RepositoryId repositoryId;

        Builder()
        {
        }

        @SuppressWarnings("unchecked")
        public B blobStore( final BlobStore val )
        {
            blobStore = val;
            return (B) this;
        }

        @SuppressWarnings("unchecked")
        public B nodeService( final NodeService val )
        {
            nodeService = val;
            return (B) this;
        }

        @SuppressWarnings("unchecked")
        public B dumpReader( final DumpReader val )
        {
            dumpReader = val;
            return (B) this;
        }

        @SuppressWarnings("unchecked")
        public B repositoryId( final RepositoryId val )
        {
            repositoryId = val;
            return (B) this;
        }
    }
}
