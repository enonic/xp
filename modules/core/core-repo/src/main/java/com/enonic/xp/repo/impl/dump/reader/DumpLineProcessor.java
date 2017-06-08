package com.enonic.xp.repo.impl.dump.reader;

import java.io.IOException;
import java.util.Collection;

import com.google.common.io.LineProcessor;

import com.enonic.xp.node.AttachedBinaries;
import com.enonic.xp.node.BinaryAttachment;
import com.enonic.xp.node.BinaryAttachments;
import com.enonic.xp.node.ImportNodeParams;
import com.enonic.xp.node.ImportNodeVersionParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeBranchEntry;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.node.NodeVersion;
import com.enonic.xp.repo.impl.branch.storage.NodeFactory;
import com.enonic.xp.repo.impl.dump.model.DumpEntry;
import com.enonic.xp.repo.impl.dump.model.Meta;
import com.enonic.xp.repo.impl.dump.serializer.DumpEntrySerializer;
import com.enonic.xp.repo.impl.dump.serializer.json.DumpEntryJsonSerializer;

public class DumpLineProcessor
    implements LineProcessor<EntryLoadResult>
{
    private final EntryLoadResult result;

    private final NodeService nodeService;

    private final DumpReader dumpReader;

    private final DumpEntrySerializer serializer;

    private final boolean includeVersions;

    private DumpLineProcessor( final Builder builder )
    {
        result = builder.result;
        nodeService = builder.nodeService;
        dumpReader = builder.dumpReader;
        serializer = new DumpEntryJsonSerializer();
        includeVersions = builder.includeVersions;
    }

    @Override
    public boolean processLine( final String line )
        throws IOException
    {
        final DumpEntry dumpEntry = this.serializer.deSerialize( line );

        final Collection<Meta> versions = dumpEntry.getVersions();

        for ( final Meta meta : versions )
        {
            if ( meta.isCurrent() )
            {
                final NodeVersion nodeVersion = this.dumpReader.get( meta.getVersion() );
                final BinaryAttachments.Builder binaryAttachments = getBinaryAttachments( nodeVersion );

                final Node node = NodeFactory.create( nodeVersion, NodeBranchEntry.create().
                    nodeId( dumpEntry.getNodeId() ).
                    nodePath( meta.getNodePath() ).
                    nodeVersionId( meta.getVersion() ).
                    timestamp( meta.getTimestamp() ).
                    nodeState( meta.getNodeState() ).
                    build() );

                this.nodeService.importNode( ImportNodeParams.create().
                    importNode( node ).
                    importPermissions( true ).
                    binaryAttachments( binaryAttachments.build() ).
                    build() );
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
            }
        }

        return true;
    }

    private BinaryAttachments.Builder getBinaryAttachments( final NodeVersion nodeVersion )
    {
        final BinaryAttachments.Builder binaryAttachments = BinaryAttachments.create();
        final AttachedBinaries attachedBinaries = nodeVersion.getAttachedBinaries();
        attachedBinaries.forEach( attachedBinary -> binaryAttachments.add(
            new BinaryAttachment( attachedBinary.getBinaryReference(), this.dumpReader.getBinary( attachedBinary.getBlobKey() ) ) ) );
        return binaryAttachments;
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

        public DumpLineProcessor build()
        {
            return new DumpLineProcessor( this );
        }
    }
}
