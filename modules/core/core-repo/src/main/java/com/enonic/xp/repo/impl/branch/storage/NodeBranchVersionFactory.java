package com.enonic.xp.repo.impl.branch.storage;

import java.time.Instant;

import com.enonic.xp.blob.BlobKey;
import com.enonic.xp.node.NodeVersionKey;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeVersionId;
import com.enonic.xp.repo.impl.NodeBranchEntry;
import com.enonic.xp.repo.impl.ReturnValues;

public class NodeBranchVersionFactory
{
    public static NodeBranchEntry create( final ReturnValues returnValues )
    {
        final Object path = returnValues.getSingleValue( BranchIndexPath.PATH.getPath() );
        final Object versionId = returnValues.getSingleValue( BranchIndexPath.VERSION_ID.getPath() );
        final Object nodeBlobKey = returnValues.getSingleValue( BranchIndexPath.NODE_BLOB_KEY.getPath() );
        final Object indexConfigBlobKey = returnValues.getSingleValue( BranchIndexPath.INDEX_CONFIG_BLOB_KEY.getPath() );
        final Object accessControlBlobKey = returnValues.getSingleValue( BranchIndexPath.ACCESS_CONTROL_BLOB_KEY.getPath() );
        final Object timestamp = returnValues.getSingleValue( BranchIndexPath.TIMESTAMP.getPath() );
        final Object nodeId = returnValues.getSingleValue( BranchIndexPath.NODE_ID.getPath() );

        return NodeBranchEntry.create()
            .nodePath( path != null ? new NodePath( path.toString() ) : NodePath.ROOT )
            .nodeVersionId( NodeVersionId.from( versionId ) )
            .nodeVersionKey( NodeVersionKey.create()
                                 .nodeBlobKey( BlobKey.from( nodeBlobKey.toString() ) )
                                 .indexConfigBlobKey( BlobKey.from( indexConfigBlobKey.toString() ) )
                                 .accessControlBlobKey( BlobKey.from( accessControlBlobKey.toString() ) )
                                 .build() )
            .timestamp( Instant.parse( timestamp.toString() ) )
            .nodeId( NodeId.from( nodeId ) )
            .build();
    }
}
