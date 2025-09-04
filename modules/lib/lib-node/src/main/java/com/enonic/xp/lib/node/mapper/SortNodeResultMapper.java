package com.enonic.xp.lib.node.mapper;

import com.enonic.xp.node.Node;
import com.enonic.xp.node.SortNodeResult;
import com.enonic.xp.script.serializer.MapGenerator;
import com.enonic.xp.script.serializer.MapSerializable;

public class SortNodeResultMapper
    implements MapSerializable
{
    SortNodeResult  sortNodeResult;

    public SortNodeResultMapper( final SortNodeResult sortNodeResult )
    {
        this.sortNodeResult = sortNodeResult;
    }

    @Override
    public void serialize( final MapGenerator gen )
    {
        gen.value( "node", new NodeMapper( sortNodeResult.getNode() ) );
        gen.array( "reorderedNodes" );
        for ( final Node reorderedNode : sortNodeResult.getReorderedNodes() )
        {
            gen.map();
            gen.value( "node", new NodeMapper( reorderedNode ) );
            gen.end();
        }
        gen.end();
    }
}
