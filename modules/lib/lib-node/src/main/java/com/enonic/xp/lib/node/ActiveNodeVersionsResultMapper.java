package com.enonic.xp.lib.node;

import java.util.Map;

import com.google.common.collect.ImmutableMap;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.node.GetActiveNodeVersionsResult;
import com.enonic.xp.node.NodeVersionMetadata;
import com.enonic.xp.script.serializer.MapGenerator;
import com.enonic.xp.script.serializer.MapSerializable;

public class ActiveNodeVersionsResultMapper
    implements MapSerializable
{
    private final ImmutableMap<Branch, NodeVersionMetadata> nodeVersions;

    public ActiveNodeVersionsResultMapper( final GetActiveNodeVersionsResult result )
    {
        this.nodeVersions = result.getNodeVersions();
    }

    @Override
    public void serialize( final MapGenerator gen )
    {
        for ( Map.Entry<Branch, NodeVersionMetadata> entry : nodeVersions.entrySet() )
        {
            gen.value( entry.getKey().toString(), new NodeVersionMapper( entry.getValue() ) );
        }
    }
}
