package com.enonic.wem.repo.internal.elasticsearch.branch;

import java.time.Instant;

import com.enonic.wem.repo.internal.index.result.GetResult;
import com.enonic.wem.repo.internal.index.result.ReturnValues;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeState;
import com.enonic.xp.node.NodeVersionId;

public class NodeBranchVersionFactory
{
    public static NodeBranchVersion create( final GetResult getResult )
    {
        final ReturnValues resultFields = getResult.getReturnValues();

        return NodeBranchVersion.create().
            nodePath( NodePath.create( resultFields.getSingleValue( BranchIndexPath.PATH.getPath() ).toString() ).build() ).
            nodeState( NodeState.from( resultFields.getSingleValue( BranchIndexPath.STATE.getPath() ).toString() ) ).
            nodeVersionId( NodeVersionId.from( resultFields.getSingleValue( BranchIndexPath.VERSION_ID.getPath() ).toString() ) ).
            timestamp( Instant.parse( resultFields.getSingleValue( BranchIndexPath.TIMESTAMP.getPath() ).toString() ) ).
            build();
    }

}
