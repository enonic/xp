package com.enonic.xp.lib.node.mapper;

import com.enonic.xp.node.PushNodeEntry;
import com.enonic.xp.node.PushNodesResult;
import com.enonic.xp.script.serializer.MapGenerator;
import com.enonic.xp.script.serializer.MapSerializable;

public class PushNodesResultMapper
    implements MapSerializable
{
    private final PushNodesResult result;

    public PushNodesResultMapper( final PushNodesResult result )
    {
        this.result = result;
    }

    @Override
    public void serialize( final MapGenerator gen )
    {
        addSuccessful( gen );
        addFailed( gen );
    }

    private void addFailed( final MapGenerator gen )
    {
        gen.array( "failed" );

        for ( final PushNodesResult.Failed entry : this.result.getFailedEntries() )
        {
            gen.map();
            gen.value( "id", entry.getNodeBranchEntry().getNodeId() );
            gen.value( "reason", entry.getReason().toString() );
            gen.end();
        }

        gen.end();
    }

    private void addSuccessful( final MapGenerator gen )
    {
        gen.array( "success" );
        for ( final PushNodeEntry success : result.getSuccessfulEntries() )
        {
            gen.value( success.getNodeBranchEntry().getNodeId() );
        }
        gen.end();
    }

}
