package com.enonic.xp.repo.impl.branch.storage;

import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeVersion;
import com.enonic.xp.node.RootNode;

public class NodeFactory
{
    public static final Node create( final NodeVersion nodeVersion, final BranchNodeVersion branchNodeVersion )
    {
        if ( branchNodeVersion.getNodeId().equals( RootNode.UUID ) )
        {
            return RootNode.create().
                permissions( nodeVersion.getPermissions() ).
                childOrder( nodeVersion.getChildOrder() ).
                build();
        }

        return Node.create( nodeVersion ).
            parentPath( branchNodeVersion.getNodePath().getParentPath() ).
            name( branchNodeVersion.getNodePath().getLastElement().toString() ).
            timestamp( branchNodeVersion.getTimestamp() ).
            nodeState( branchNodeVersion.getNodeState() ).
            nodeVersionId( branchNodeVersion.getVersionId() ).
            build();
    }
}
