package com.enonic.xp.lib.node;

import com.enonic.xp.node.NodeVersionMetadata;
import com.enonic.xp.node.NodeVersionMetadatas;
import com.enonic.xp.node.NodeVersionQueryResult;
import com.enonic.xp.script.serializer.MapGenerator;
import com.enonic.xp.script.serializer.MapSerializable;

public class NodeVersionsQueryResultMapper
    implements MapSerializable
{
    private final NodeVersionMetadatas nodeVersions;

    private final long count;

    private final long total;

    public NodeVersionsQueryResultMapper( final NodeVersionQueryResult nodeVersionQueryResult )
    {
        this.nodeVersions = nodeVersionQueryResult.getNodeVersionMetadatas();
        this.count = nodeVersionQueryResult.getNodeVersionMetadatas().getSize();
        this.total = nodeVersionQueryResult.getTotalHits();
    }

    @Override
    public void serialize( final MapGenerator gen )
    {
        gen.value( "total", total );
        gen.value( "count", count );
        serialize( gen, nodeVersions );
    }

    private void serialize( final MapGenerator gen, final NodeVersionMetadatas nodeVersions )
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
