package com.enonic.xp.repo.impl.dump;

import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeState;
import com.enonic.xp.node.NodeVersion;
import com.enonic.xp.node.NodeVersionMetadata;
import com.enonic.xp.repo.impl.dump.model.VersionMeta;

class VersionMetaFactory
{
    public static VersionMeta create( final Node node )
    {
        return VersionMeta.create().
            timestamp( node.getTimestamp() ).
            nodePath( node.path() ).
            version( node.getNodeVersionId() ).
            nodeState( node.getNodeState() ).
            build();
    }

    public static VersionMeta create( final NodeVersion nodeVersion, final NodeVersionMetadata metaData )
    {
        return VersionMeta.create().
            timestamp( nodeVersion.getTimestamp() ).
            nodePath( metaData.getNodePath() ).
            version( nodeVersion.getVersionId() ).
            nodeState( NodeState.DEFAULT ).
            build();
    }
}
