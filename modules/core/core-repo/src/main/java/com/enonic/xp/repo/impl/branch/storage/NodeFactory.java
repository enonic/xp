package com.enonic.xp.repo.impl.branch.storage;

import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeVersion;
import com.enonic.xp.node.NodeVersionMetadata;
import com.enonic.xp.repo.impl.NodeBranchEntry;


public class NodeFactory
{
    public static Node create( final NodeVersion nodeVersion, final NodeBranchEntry nodeBranchEntry )
    {
        final Node.Builder builder =
            Node.create( nodeVersion ).nodeVersionId( nodeBranchEntry.getVersionId() ).timestamp( nodeBranchEntry.getTimestamp() );
        if ( !Node.ROOT_UUID.equals( nodeVersion.getId() ) )
        {
            builder.parentPath( nodeBranchEntry.getNodePath().getParentPath() ).name( nodeBranchEntry.getNodePath().getName() );
        }
        return builder.build();
    }

    public static Node create( final NodeVersion nodeVersion, final NodeVersionMetadata nodeVersionMetadata )
    {
        final Node.Builder builder = Node.create( nodeVersion )
            .nodeVersionId( nodeVersionMetadata.getNodeVersionId() )
            .timestamp( nodeVersionMetadata.getTimestamp() );

        if ( !Node.ROOT_UUID.equals( nodeVersion.getId() ) )
        {
            builder.parentPath( nodeVersionMetadata.getNodePath().getParentPath() ).name( nodeVersionMetadata.getNodePath().getName() );
        }
        return builder.build();
    }
}
