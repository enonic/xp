package com.enonic.xp.lib.node.mapper;

import com.enonic.xp.node.FindNodesByParentResult;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.script.serializer.MapGenerator;
import com.enonic.xp.script.serializer.MapSerializable;

public class FindNodesByParentResultMapper
    implements MapSerializable
{
    private final FindNodesByParentResult result;

    public FindNodesByParentResultMapper( final FindNodesByParentResult result )
    {
        this.result = result;
    }

    @Override
    public void serialize( final MapGenerator gen )
    {
        gen.value( "total", result.getTotalHits() );
        gen.value( "count", result.getNodeIds().getSize() );
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
