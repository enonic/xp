package com.enonic.xp.lib.node.mapper;

import com.enonic.xp.node.NodeComparison;
import com.enonic.xp.node.ResolveSyncWorkResult;
import com.enonic.xp.script.serializer.MapGenerator;
import com.enonic.xp.script.serializer.MapSerializable;

public class ResolveSyncWorkResultMapper
    implements MapSerializable
{

    private final ResolveSyncWorkResult result;

    public ResolveSyncWorkResultMapper( final ResolveSyncWorkResult result )
    {
        this.result = result;
    }

    @Override
    public void serialize( final MapGenerator gen )
    {
        gen.array( "diff" );

        for ( final NodeComparison entry : result.getNodeComparisons() )
        {
            gen.map();
            gen.value( "id", entry.getNodeId() );
            gen.value( "status", entry.getCompareStatus().toString() );
            gen.end();
        }

        gen.end();
    }
}
