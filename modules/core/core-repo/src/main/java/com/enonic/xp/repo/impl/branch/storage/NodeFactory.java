package com.enonic.xp.repo.impl.branch.storage;

import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeVersion;
import com.enonic.xp.node.RootNode;

public class NodeFactory
{
    public static final Node create( final NodeVersion nodeVersion, final NodeBranchMetadata nodeBranchMetadata )
    {
        if ( nodeBranchMetadata.getNodeId().equals( RootNode.UUID ) )
        {
            return RootNode.create().
                permissions( nodeVersion.getPermissions() ).
                childOrder( nodeVersion.getChildOrder() ).
                build();
        }

        return Node.create( nodeVersion ).
            parentPath( nodeBranchMetadata.getNodePath().getParentPath() ).
            name( nodeBranchMetadata.getNodePath().getLastElement().toString() ).
            timestamp( nodeBranchMetadata.getTimestamp() ).
            nodeState( nodeBranchMetadata.getNodeState() ).
            nodeVersionId( nodeBranchMetadata.getVersionId() ).
            build();
    }
}
