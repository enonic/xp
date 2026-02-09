package com.enonic.xp.repo.impl.branch.storage;

import java.time.Instant;

import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeVersion;
import com.enonic.xp.node.NodeVersionId;
import com.enonic.xp.repo.impl.NodeBranchEntry;
import com.enonic.xp.repo.impl.NodeStoreVersion;


public class NodeFactory
{
    public static Node create( final NodeStoreVersion nodeStoreVersion, final NodeBranchEntry nodeBranchEntry )
    {
        return create( nodeStoreVersion, nodeBranchEntry.getVersionId(), nodeBranchEntry.getNodePath(), nodeBranchEntry.getTimestamp() );
    }

    public static Node create( final NodeStoreVersion nodeStoreVersion, final NodeVersion nodeVersion )
    {
        return create( nodeStoreVersion, nodeVersion.getNodeVersionId(), nodeVersion.getNodePath(), nodeVersion.getTimestamp() );
    }

    public static Node create( final NodeStoreVersion nodeStoreVersion, final NodeVersionId nodeVersionId, final NodePath nodePath,
                               final Instant timestamp )
    {
        final Node.Builder builder = create( nodeStoreVersion ).nodeVersionId( nodeVersionId ).timestamp( timestamp );
        if ( !Node.ROOT_UUID.equals( nodeStoreVersion.id() ) )
        {
            builder.parentPath( nodePath.getParentPath() ).name( nodePath.getName() );
        }
        return builder.build();
    }

    public static Node.Builder create( final NodeStoreVersion nodeStoreVersion )
    {
        return Node.create()
            .id( nodeStoreVersion.id() )
            .nodeType( nodeStoreVersion.nodeType() )
            .data( nodeStoreVersion.data() )
            .indexConfigDocument( nodeStoreVersion.indexConfigDocument() )
            .childOrder( nodeStoreVersion.childOrder() )
            .manualOrderValue( nodeStoreVersion.manualOrderValue() )
            .permissions( nodeStoreVersion.permissions() )
            .attachedBinaries( nodeStoreVersion.attachedBinaries() );
    }
}
