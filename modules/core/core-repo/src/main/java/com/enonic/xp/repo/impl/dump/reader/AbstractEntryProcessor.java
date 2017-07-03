package com.enonic.xp.repo.impl.dump.reader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.ByteSource;

import com.enonic.xp.blob.BlobKey;
import com.enonic.xp.blob.BlobRecord;
import com.enonic.xp.blob.BlobStore;
import com.enonic.xp.node.AttachedBinary;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.node.NodeVersion;
import com.enonic.xp.repo.impl.dump.RepoLoadException;
import com.enonic.xp.repo.impl.dump.model.VersionMeta;
import com.enonic.xp.repo.impl.dump.serializer.DumpSerializer;
import com.enonic.xp.repo.impl.dump.serializer.json.JsonDumpSerializer;
import com.enonic.xp.repo.impl.node.NodeConstants;

class AbstractEntryProcessor
{
    private final static Logger LOG = LoggerFactory.getLogger( AbstractEntryProcessor.class );

    private final BlobStore blobStore;

    final NodeService nodeService;

    final DumpReader dumpReader;

    final DumpSerializer serializer;

    AbstractEntryProcessor( final Builder builder )
    {
        blobStore = builder.blobStore;
        nodeService = builder.nodeService;
        dumpReader = builder.dumpReader;
        serializer = new JsonDumpSerializer();
    }

    void validateOrAddBinary( final NodeVersion nodeVersion, final EntryLoadResult.Builder result )
    {
        nodeVersion.getAttachedBinaries().forEach( binary -> {

            final BlobRecord existingRecord = this.blobStore.getRecord( NodeConstants.BINARY_SEGMENT, BlobKey.from( binary.getBlobKey() ) );

            if ( existingRecord == null )
            {
                try
                {
                    final ByteSource dumpBinary = this.dumpReader.getBinary( binary.getBlobKey() );
                    this.blobStore.addRecord( NodeConstants.BINARY_SEGMENT, dumpBinary );
                }
                catch ( RepoLoadException e )
                {
                    reportBinaryError( nodeVersion, result, binary, e );
                }
            }
        } );
    }

    private void reportBinaryError( final NodeVersion nodeVersion, final EntryLoadResult.Builder result, final AttachedBinary binary,
                                    final RepoLoadException e )
    {
        final String message = String.format( "Failed to load binary for nodeId %s, blobKey %s", nodeVersion.getId(), binary.getBlobKey() );
        result.error( EntryLoadError.error( message ) );
        LOG.error( "Cannot load binary, missing in existing blobStore, and not present in dump: " + binary.getBlobKey(), e );
    }

    void reportVersionError( final EntryLoadResult.Builder result, final VersionMeta meta )
    {
        final String message =
            String.format( "Failed to load version for node with path %s, blobKey %s", meta.getNodePath(), meta.getVersion() );
        result.error( EntryLoadError.error( message ) );
    }


    public static class Builder<B extends Builder>
    {
        private BlobStore blobStore;

        private NodeService nodeService;

        private DumpReader dumpReader;

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
    }
}
