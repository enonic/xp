package com.enonic.xp.lib.node;

import com.enonic.xp.node.GetNodeVersionsResult;
import com.enonic.xp.node.NodeVersion;
import com.enonic.xp.node.NodeVersions;
import com.enonic.xp.script.serializer.MapGenerator;
import com.enonic.xp.script.serializer.MapSerializable;

public class NodeVersionsQueryResultMapper
    implements MapSerializable
{
    private final NodeVersions nodeVersions;

    private final long count;

    private final long total;

    private final String cursor;

    public NodeVersionsQueryResultMapper( final GetNodeVersionsResult result )
    {
        this.nodeVersions = result.getNodeVersions();
        this.count = result.getNodeVersions().getSize();
        this.total = result.getTotalHits();
        this.cursor = result.getCursor();
    }

    @Override
    public void serialize( final MapGenerator gen )
    {
        gen.value( "total", total );
        gen.value( "count", count );
        gen.value( "cursor", cursor );
        serialize( gen, nodeVersions );
    }

    private void serialize( final MapGenerator gen, final NodeVersions nodeVersions )
    {
        gen.array( "hits" );
        for ( NodeVersion nodeVersion : nodeVersions )
        {
            gen.map();
            new NodeVersionMapper( nodeVersion ).serialize( gen );
            gen.end();
        }
        gen.end();
    }
}
