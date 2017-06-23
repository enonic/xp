package com.enonic.xp.repo.impl.dump;

import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeState;
import com.enonic.xp.node.NodeVersionMetadata;
import com.enonic.xp.repo.impl.dump.model.Meta;

class MetaFactory
{
    public static Meta create( final Node node, final boolean current )
    {
        return Meta.create().
            timestamp( node.getTimestamp() ).
            nodePath( node.path() ).
            version( node.getNodeVersionId() ).
            nodeState( node.getNodeState() ).
            current( current ).
            build();
    }

    public static Meta create( final NodeVersionMetadata metaData, final boolean current )
    {
        return Meta.create().
            timestamp( metaData.getTimestamp() ).
            nodePath( metaData.getNodePath() ).
            version( metaData.getNodeVersionId() ).
            nodeState( NodeState.DEFAULT ).
            current( current ).
            build();
    }

}
