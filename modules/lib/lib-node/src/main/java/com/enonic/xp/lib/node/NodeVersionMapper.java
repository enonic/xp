package com.enonic.xp.lib.node;

import com.enonic.xp.node.NodeVersionMetadata;
import com.enonic.xp.script.serializer.MapGenerator;
import com.enonic.xp.script.serializer.MapSerializable;

public final class NodeVersionMapper
    implements MapSerializable
{
    private final NodeVersionMetadata value;

    public NodeVersionMapper( final NodeVersionMetadata value )
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
    }
}
