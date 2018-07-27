package com.enonic.xp.lib.node;

import com.enonic.xp.node.NodeVersionMetadata;
import com.enonic.xp.node.NodeVersionQueryResult;
import com.enonic.xp.node.NodeVersionsMetadata;
import com.enonic.xp.script.serializer.MapGenerator;
import com.enonic.xp.script.serializer.MapSerializable;

public class NodeVersionsResultMapper
    implements MapSerializable
{
    private final NodeVersionsMetadata nodeVersions;

    private final long count;

    private final long total;

    public NodeVersionsResultMapper( final NodeVersionQueryResult nodeVersionQueryResult )
    {
        this.nodeVersions = nodeVersionQueryResult.getNodeVersionsMetadata();
        this.count = nodeVersionQueryResult.getHits();
        this.total = nodeVersionQueryResult.getTotalHits();
    }

    @Override
    public void serialize( final MapGenerator gen )
    {
        gen.value( "total", this.total );
        gen.value( "count", this.count );
        serialize( gen, this.nodeVersions );
    }

    private void serialize( final MapGenerator gen, final NodeVersionsMetadata nodeVersions )
    {
        gen.array( "hits" );
        for ( NodeVersionMetadata nodeVersion : nodeVersions )
        {
            gen.map();
            new NodeVersionMapper( nodeVersion ).
                serialize( gen );
            gen.end();
        }
        gen.end();
    }
}
