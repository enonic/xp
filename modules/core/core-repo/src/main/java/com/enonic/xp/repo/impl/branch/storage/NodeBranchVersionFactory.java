package com.enonic.xp.repo.impl.branch.storage;

import java.time.Instant;

import com.enonic.xp.blob.BlobKey;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeVersionId;
import com.enonic.xp.node.NodeVersionKey;
import com.enonic.xp.repo.impl.NodeBranchEntry;
import com.enonic.xp.storage.spi.ReturnValues;
import com.enonic.xp.storage.spi.BranchEntryRecord;

public class NodeBranchVersionFactory
{
    public static NodeBranchEntry create( final ReturnValues returnValues )
    {
        final NodePath path =
            returnValues.getOptional( BranchIndexPath.PATH ).map( Object::toString ).map( NodePath::new ).orElse( NodePath.ROOT );
        final NodeVersionId versionId = NodeVersionId.from( returnValues.getStringValue( BranchIndexPath.VERSION_ID ) );
        final BlobKey nodeBlobKey = BlobKey.from( returnValues.getStringValue( BranchIndexPath.NODE_BLOB_KEY ) );
        final BlobKey indexConfigBlobKey = BlobKey.from( returnValues.getStringValue( BranchIndexPath.INDEX_CONFIG_BLOB_KEY ) );
        final BlobKey accessControlBlobKey = BlobKey.from( returnValues.getStringValue( BranchIndexPath.ACCESS_CONTROL_BLOB_KEY ) );
        final Instant timestamp = Instant.parse( returnValues.getStringValue( BranchIndexPath.TIMESTAMP ) );
        final NodeId nodeId = NodeId.from( returnValues.getStringValue( BranchIndexPath.NODE_ID ) );

        return NodeBranchEntry.create()
            .nodePath( path )
            .nodeVersionId( versionId )
            .nodeVersionKey( NodeVersionKey.create()
                                 .nodeBlobKey( nodeBlobKey )
                                 .indexConfigBlobKey( indexConfigBlobKey )
                                 .accessControlBlobKey( accessControlBlobKey )
                                 .build() )
            .timestamp( timestamp )
            .nodeId( nodeId )
            .build();
    }

    /** Adapts a domain {@link NodeBranchEntry} onto the SPI {@link BranchEntryRecord}, for {@code NodeStore} store/get results. */
    public static BranchEntryRecord toRecord( final NodeBranchEntry entry )
    {
        return new BranchEntryRecord( entry.getNodeId().toString(), entry.getNodePath().toString(), entry.getVersionId().toString(),
                                       entry.getNodeVersionKey().getNodeBlobKey().toString(),
                                       entry.getNodeVersionKey().getIndexConfigBlobKey().toString(),
                                       entry.getNodeVersionKey().getAccessControlBlobKey().toString(), entry.getTimestamp() );
    }

    /** Inverse of {@link #toRecord}, for consumers of {@code NodeStore} rebuilding the domain object. */
    public static NodeBranchEntry fromRecord( final BranchEntryRecord record )
    {
        return NodeBranchEntry.create()
            .nodeId( NodeId.from( record.nodeId() ) )
            .nodePath( new NodePath( record.nodePath() ) )
            .nodeVersionId( NodeVersionId.from( record.versionId() ) )
            .nodeVersionKey( NodeVersionKey.create()
                                 .nodeBlobKey( BlobKey.from( record.nodeDataHash() ) )
                                 .indexConfigBlobKey( BlobKey.from( record.indexConfigHash() ) )
                                 .accessControlBlobKey( BlobKey.from( record.aclHash() ) )
                                 .build() )
            .timestamp( record.timestamp() )
            .build();
    }
}
