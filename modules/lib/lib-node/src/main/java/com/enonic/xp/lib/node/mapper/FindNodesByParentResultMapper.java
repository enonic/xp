package com.enonic.xp.lib.node.mapper;

import com.enonic.xp.node.FindNodesByParentResult;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.script.serializer.MapGenerator;
import com.enonic.xp.script.serializer.MapSerializable;

public class FindNodesByParentResultMapper
    implements MapSerializable
{
    private FindNodesByParentResult result;

    private final int count;

    public FindNodesByParentResultMapper( final int count, final FindNodesByParentResult result )
    {
        this.count = count;
        this.result = result;
    }

    @Override
    public void serialize( final MapGenerator gen )
    {
        gen.value( "total", result.getTotalHits() );
        gen.value( "count", count );
        gen.value( "hits", result.getHits() );
        serialize( gen, result.getNodeIds() );
    }

    private void serialize( final MapGenerator gen, final NodeIds nodeIds )
    {
        gen.array( "hits" );
        for ( NodeId nodeId : nodeIds )
        {
            gen.map();
            gen.value( "id", nodeId );
            gen.end();
        }
        gen.end();
    }
}
