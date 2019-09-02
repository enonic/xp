package com.enonic.xp.repo.impl.branch.storage;

import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeBranchEntry;
import com.enonic.xp.node.NodeState;
import com.enonic.xp.node.NodeVersion;
import com.enonic.xp.node.NodeVersionMetadata;


public class NodeFactory
{
    public static Node create( final NodeVersion nodeVersion, final NodeBranchEntry nodeBranchEntry )
    {
        if ( nodeBranchEntry.getNodeId().equals( Node.ROOT_UUID ) )
        {
            return Node.createRoot().
                nodeType( nodeVersion.getNodeType() ).
                nodeVersionId( nodeBranchEntry.getVersionId() ).
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

    public static Node create( final NodeVersion nodeVersion, final NodeVersionMetadata nodeVersionMetadata )
    {
        final Node.Builder builder = Node.create( nodeVersion ).
            nodeState( NodeState.DEFAULT ).
            path( nodeVersionMetadata.getNodePath().toString() ).
            nodeVersionId( nodeVersionMetadata.getNodeVersionId() ).
            parentPath( nodeVersionMetadata.getNodePath().getParentPath() ).
            name( nodeVersionMetadata.getNodePath().getName() ).
            timestamp( nodeVersionMetadata.getTimestamp() );

        if ( Node.ROOT_UUID.equals( nodeVersionMetadata.getNodeId() ) )
        {
            builder.inheritPermissions( false );
        }

        return builder.build();
    }

}
