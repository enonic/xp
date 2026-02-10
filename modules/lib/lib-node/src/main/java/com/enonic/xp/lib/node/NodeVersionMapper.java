package com.enonic.xp.lib.node;

import com.enonic.xp.node.NodeVersion;
import com.enonic.xp.script.serializer.MapGenerator;
import com.enonic.xp.script.serializer.MapSerializable;

public final class NodeVersionMapper
    implements MapSerializable
{
    private final NodeVersion value;

    public NodeVersionMapper( final NodeVersion value )
    {
        this.value = value;
    }

    @Override
    public void serialize( final MapGenerator gen )
    {
        gen.value( "versionId", value.getNodeVersionId() );
        gen.value( "nodeId", value.getNodeId() );
        gen.value( "nodePath", value.getNodePath() );
        gen.value( "timestamp", value.getTimestamp() );
        gen.value( "commitId", value.getNodeCommitId() );
    }
}
