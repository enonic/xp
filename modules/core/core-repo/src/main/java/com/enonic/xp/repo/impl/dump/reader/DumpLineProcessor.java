package com.enonic.xp.repo.impl.dump.reader;

import java.io.IOException;
import java.util.Collection;

import com.google.common.io.ByteSource;
import com.google.common.io.LineProcessor;

import com.enonic.xp.blob.BlobKey;
import com.enonic.xp.blob.BlobRecord;
import com.enonic.xp.blob.BlobStore;
import com.enonic.xp.node.ImportNodeVersionParams;
import com.enonic.xp.node.LoadNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeBranchEntry;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.node.NodeVersion;
import com.enonic.xp.repo.impl.branch.storage.NodeFactory;
import com.enonic.xp.repo.impl.dump.RepoDumpException;
import com.enonic.xp.repo.impl.dump.model.DumpEntry;
import com.enonic.xp.repo.impl.dump.model.Meta;
import com.enonic.xp.repo.impl.dump.serializer.DumpEntrySerializer;
import com.enonic.xp.repo.impl.dump.serializer.json.DumpEntryJsonSerializer;
import com.enonic.xp.repo.impl.node.NodeConstants;

public class DumpLineProcessor
    implements LineProcessor<EntryLoadResult>
{
    private EntryLoadResult result;

    private final BlobStore blobStore;

    private final NodeService nodeService;

    private final DumpReader dumpReader;

    private final DumpEntrySerializer serializer;

    private final boolean includeVersions;

    private DumpLineProcessor( final Builder builder )
    {
        result = builder.result;
        nodeService = builder.nodeService;
        dumpReader = builder.dumpReader;
        blobStore = builder.blobStore;
        serializer = new DumpEntryJsonSerializer();
        includeVersions = builder.includeVersions;
    }

    @Override
    public boolean processLine( final String line )
        throws IOException
    {
        final EntryLoadResult.Builder result = EntryLoadResult.create();

        final DumpEntry dumpEntry = this.serializer.deSerialize( line );

        final Collection<Meta> versions = dumpEntry.getVersions();

        for ( final Meta meta : versions )
        {
            if ( meta.isCurrent() )
            {
                final NodeVersion nodeVersion = this.dumpReader.get( meta.getVersion() );

                final Node node = NodeFactory.create( nodeVersion, NodeBranchEntry.create().
                    nodeId( dumpEntry.getNodeId() ).
                    nodePath( meta.getNodePath() ).
                    nodeVersionId( meta.getVersion() ).
                    timestamp( meta.getTimestamp() ).
                    nodeState( meta.getNodeState() ).
                    build() );

                this.nodeService.loadNode( LoadNodeParams.create().
                    node( node ).
                    build() );

                validateOrAddBinary( nodeVersion );

                result.addedVersion();
            }
            else if ( includeVersions )
            {
                final NodeVersion nodeVersion = this.dumpReader.get( meta.getVersion() );

                this.nodeService.importNodeVersion( ImportNodeVersionParams.create().
                    nodeId( dumpEntry.getNodeId() ).
                    timestamp( meta.getTimestamp() ).
                    nodePath( meta.getNodePath() ).
                    nodeVersion( nodeVersion ).
                    build() );

                validateOrAddBinary( nodeVersion );

                result.addedVersion();
            }
        }

        this.result = result.build();
        return true;
    }

    private void validateOrAddBinary( final NodeVersion nodeVersion )
    {
        nodeVersion.getAttachedBinaries().forEach( binary -> {

            final BlobRecord existingRecord = this.blobStore.getRecord( NodeConstants.BINARY_SEGMENT, BlobKey.from( binary.getBlobKey() ) );

            if ( existingRecord == null )
            {
                final ByteSource dumpBinary = this.dumpReader.getBinary( binary.getBlobKey() );

                if ( dumpBinary == null )
                {
                    throw new RepoDumpException(
                        "Cannot load binary, missing in existing blobStore, and not present in dump: " + binary.getBlobKey() );
                }

                this.blobStore.addRecord( NodeConstants.BINARY_SEGMENT, dumpBinary );
            }
        } );
    }

    @Override
    public EntryLoadResult getResult()
    {
        return result;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private EntryLoadResult result;

        private NodeService nodeService;

        private DumpReader dumpReader;

        private BlobStore blobStore;

        private boolean includeVersions;

        private Builder()
        {
        }

        public Builder result( final EntryLoadResult val )
        {
            result = val;
            return this;
        }

        public Builder nodeService( final NodeService val )
        {
            nodeService = val;
            return this;
        }

        public Builder dumpReader( final DumpReader val )
        {
            dumpReader = val;
            return this;
        }

        public Builder includeVersions( final boolean val )
        {
            includeVersions = val;
            return this;
        }

        public Builder blobStore( final BlobStore blobStore )
        {
            this.blobStore = blobStore;
            return this;
        }

        public DumpLineProcessor build()
        {
            return new DumpLineProcessor( this );
        }
    }
}
