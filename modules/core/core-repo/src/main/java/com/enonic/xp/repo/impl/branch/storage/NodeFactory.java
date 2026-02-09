package com.enonic.xp.repo.impl.branch.storage;

import java.time.Instant;

import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.repo.impl.NodeStoreVersion;
import com.enonic.xp.node.NodeVersionId;
import com.enonic.xp.node.NodeVersion;
import com.enonic.xp.repo.impl.NodeBranchEntry;


public class NodeFactory
{
    public static Node create( final NodeStoreVersion nodeVersion, final NodeBranchEntry nodeBranchEntry )
    {
        return create( nodeVersion, nodeBranchEntry.getVersionId(), nodeBranchEntry.getNodePath(),
                       nodeBranchEntry.getTimestamp() );
    }

    public static Node create( final NodeStoreVersion nodeVersion, final NodeVersion nodeVersionMetadata )
    {
        return create( nodeVersion, nodeVersionMetadata.getNodeVersionId(), nodeVersionMetadata.getNodePath(),
                       nodeVersionMetadata.getTimestamp() );
    }

    public static Node create( final NodeStoreVersion nodeVersion, final NodeVersionId nodeVersionId, final NodePath nodePath,
                               final Instant timestamp )
    {
        final Node.Builder builder = create( nodeVersion ).nodeVersionId( nodeVersionId ).timestamp( timestamp );
        if ( !Node.ROOT_UUID.equals( nodeVersion.getId() ) )
        {
            builder.parentPath( nodePath.getParentPath() ).name( nodePath.getName() );
        }
        return builder.build();
    }

    public static Node.Builder create( final NodeStoreVersion nodeVersion )
    {
        return Node.create().
            id( nodeVersion.getId() ).
            nodeType( nodeVersion.getNodeType() ).
            data( nodeVersion.getData() ).
            indexConfigDocument( nodeVersion.getIndexConfigDocument() ).
            childOrder( nodeVersion.getChildOrder() ).
            manualOrderValue( nodeVersion.getManualOrderValue() ).
            permissions( nodeVersion.getPermissions() ).
            attachedBinaries( nodeVersion.getAttachedBinaries() );
    }
}
