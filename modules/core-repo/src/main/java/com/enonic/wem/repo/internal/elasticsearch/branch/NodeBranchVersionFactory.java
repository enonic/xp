package com.enonic.wem.repo.internal.elasticsearch.branch;

import java.time.Instant;

import com.enonic.wem.repo.internal.index.result.GetResultNew;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeState;
import com.enonic.xp.node.NodeVersionId;

public class NodeBranchVersionFactory
{
    public static NodeBranchVersion create( final GetResultNew getResult )
    {
        return NodeBranchVersion.create().
            nodePath( NodePath.create( getResult.getSingleValue( BranchIndexPath.PATH.getPath() ).toString() ).build() ).
            nodeState( NodeState.from( getResult.getSingleValue( BranchIndexPath.STATE.getPath() ).toString() ) ).
            nodeVersionId( NodeVersionId.from( getResult.getSingleValue( BranchIndexPath.VERSION_ID.getPath() ).toString() ) ).
            timestamp( Instant.parse( getResult.getSingleValue( BranchIndexPath.TIMESTAMP.getPath() ).toString() ) ).
            build();
    }

}
