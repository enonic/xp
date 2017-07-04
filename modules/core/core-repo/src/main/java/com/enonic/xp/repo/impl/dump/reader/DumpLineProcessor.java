package com.enonic.xp.repo.impl.dump.reader;

import java.io.IOException;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.ByteSource;
import com.google.common.io.LineProcessor;

import com.enonic.xp.blob.BlobKey;
import com.enonic.xp.blob.BlobRecord;
import com.enonic.xp.blob.BlobStore;
import com.enonic.xp.node.AttachedBinary;
import com.enonic.xp.node.ImportNodeVersionParams;
import com.enonic.xp.node.LoadNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeBranchEntry;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.node.NodeVersion;
import com.enonic.xp.repo.impl.branch.storage.NodeFactory;
import com.enonic.xp.repo.impl.dump.RepoLoadException;
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

    private final static Logger LOG = LoggerFactory.getLogger( DumpLineProcessor.class );

    private DumpLineProcessor( final Builder builder )
    {
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
                addCurrentNode( result, dumpEntry, meta );
            }
            else if ( includeVersions )
            {
                addVersion( result, dumpEntry, meta );
            }
        }

        this.result = result.build();
        return true;
    }

    private void addCurrentNode( final EntryLoadResult.Builder result, final DumpEntry dumpEntry, final Meta meta )
    {
        final NodeVersion nodeVersion = getVersion( meta );

        if ( nodeVersion == null )
        {
            reportVersionError( result, meta );
            return;
        }

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

        validateOrAddBinary( nodeVersion, result );
        result.addedVersion();
    }

    private void addVersion( final EntryLoadResult.Builder result, final DumpEntry dumpEntry, final Meta meta )
    {
        final NodeVersion nodeVersion = getVersion( meta );

        if ( nodeVersion == null )
        {
            reportVersionError( result, meta );
            return;
        }

        this.nodeService.importNodeVersion( ImportNodeVersionParams.create().
            nodeId( dumpEntry.getNodeId() ).
            timestamp( meta.getTimestamp() ).
            nodePath( meta.getNodePath() ).
            nodeVersion( nodeVersion ).
            build() );

        validateOrAddBinary( nodeVersion, result );
        result.addedVersion();
    }

    private NodeVersion getVersion( final Meta meta )
    {
        try
        {
            return this.dumpReader.get( meta.getVersion() );
        }
        catch ( RepoLoadException e )
        {
            LOG.error( "Cannot load version, missing in existing blobStore, and not present in dump: " + meta.getVersion(), e );
            return null;
        }
    }

    private void reportVersionError( final EntryLoadResult.Builder result, final Meta meta )
    {
        final String message =
            String.format( "Failed to load version for node with path %s, blobKey %s", meta.getNodePath(), meta.getVersion() );
        result.error( EntryLoadError.error( message ) );
    }

    private void validateOrAddBinary( final NodeVersion nodeVersion, final EntryLoadResult.Builder result )
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
        private NodeService nodeService;

        private DumpReader dumpReader;

        private BlobStore blobStore;

        private boolean includeVersions;

        private Builder()
        {
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
