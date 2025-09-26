package com.enonic.xp.lib.node.mapper;

import com.enonic.xp.node.PushNodeResult;
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

        for ( final PushNodeResult entry : this.result.getFailed() )
        {
            gen.map();
            gen.value( "id", entry.getNodeId() );
            gen.value( "reason", entry.getFailureReason().toString() );
            gen.end();
        }

        gen.end();
    }

    private void addSuccessful( final MapGenerator gen )
    {
        gen.array( "success" );
        for ( final PushNodeResult success : result.getSuccessful() )
        {
            gen.value( success.getNodeId() );
        }
        gen.end();
    }

}
