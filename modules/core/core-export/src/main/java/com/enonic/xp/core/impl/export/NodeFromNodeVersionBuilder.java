package com.enonic.xp.core.impl.export;

import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeVersion;
import com.enonic.xp.node.NodeVersionMetadata;

public class NodeFromNodeVersionBuilder
{

    static Node create( final NodeVersionMetadata metaData, final NodeVersion version )
    {
        return Node.create( version ).
            name( metaData.getNodePath().getName() ).
            parentPath( metaData.getNodePath().getParentPath() ).
            timestamp( metaData.getTimestamp() ).
            name( metaData.getNodePath().getName() ).
            parentPath( metaData.getNodePath().getParentPath() ).
            nodeVersionId( metaData.getNodeVersionId() ).
            blobKey( metaData.getBlobKey() ).
            build();
    }

}
