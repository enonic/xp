package com.enonic.xp.repo.impl.branch.storage;

import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeBranchEntry;
import com.enonic.xp.node.NodeVersion;


public class NodeFactory
{
    public static final Node create( final NodeVersion nodeVersion, final NodeBranchEntry nodeBranchEntry )
    {
        if ( nodeBranchEntry.getNodeId().equals( Node.ROOT_UUID ) )
        {
            return Node.createRoot().
                nodeType( nodeVersion.getNodeType() ).
                nodeVersionId( nodeVersion.getVersionId() ).
                timestamp( nodeBranchEntry.getTimestamp() ).
                permissions( nodeVersion.getPermissions() ).
                nodeState( nodeBranchEntry.getNodeState() ).
                childOrder( nodeVersion.getChildOrder() ).
                manualOrderValue( nodeVersion.getManualOrderValue() ).
                data( nodeVersion.getData() ).
                attachedBinaries( nodeVersion.getAttachedBinaries() ).
                indexConfigDocument( nodeVersion.getIndexConfigDocument() ).
                build();
        }

        return Node.create( nodeVersion ).
            parentPath( nodeBranchEntry.getNodePath().getParentPath() ).
            name( nodeBranchEntry.getNodePath().getLastElement().toString() ).
            timestamp( nodeBranchEntry.getTimestamp() ).
            nodeState( nodeBranchEntry.getNodeState() ).
            nodeVersionId( nodeBranchEntry.getVersionId() ).
            build();
    }
}
