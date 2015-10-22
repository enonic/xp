package com.enonic.xp.repo.impl.branch.storage;

import java.time.Instant;

import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeState;
import com.enonic.xp.node.NodeVersionId;
import com.enonic.xp.repo.impl.ReturnValues;

public class NodeBranchVersionFactory
{
    public static NodeBranchMetadata create( final ReturnValues returnValues )
    {
        final Object path = returnValues.getSingleValue( BranchIndexPath.PATH.getPath() );
        final Object state = returnValues.getSingleValue( BranchIndexPath.STATE.getPath() );
        final Object versionId = returnValues.getSingleValue( BranchIndexPath.VERSION_ID.getPath() );
        final Object timestamp = returnValues.getSingleValue( BranchIndexPath.TIMESTAMP.getPath() );
        final Object nodeId = returnValues.getSingleValue( BranchIndexPath.NODE_ID.getPath() );

        return NodeBranchMetadata.create().
            nodePath( path != null ? NodePath.create( path.toString() ).build() : NodePath.ROOT ).
            nodeState( state != null ? NodeState.from( state.toString() ) : NodeState.DEFAULT ).
            nodeVersionId( NodeVersionId.from( versionId.toString() ) ).
            timestamp( Instant.parse( timestamp.toString() ) ).
            nodeId( NodeId.from( nodeId.toString() ) ).
            build();
    }
}
