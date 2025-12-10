package com.enonic.xp.repo.impl.branch.storage;

import java.time.Instant;

import com.enonic.xp.blob.BlobKey;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeVersionId;
import com.enonic.xp.node.NodeVersionKey;
import com.enonic.xp.repo.impl.NodeBranchEntry;
import com.enonic.xp.repo.impl.ReturnValues;

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
}
