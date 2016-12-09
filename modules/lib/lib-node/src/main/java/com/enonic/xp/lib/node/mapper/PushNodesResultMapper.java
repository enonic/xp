package com.enonic.xp.lib.node.mapper;

import com.google.common.collect.ImmutableSet;

import com.enonic.xp.node.NodeBranchEntries;
import com.enonic.xp.node.NodeBranchEntry;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.PushNodesResult;
import com.enonic.xp.script.serializer.MapGenerator;
import com.enonic.xp.script.serializer.MapSerializable;

public class PushNodesResultMapper
    implements MapSerializable
{
    private final PushNodesResult result;

    private final NodeIds deleted;

    public PushNodesResultMapper( final PushNodesResult result, final NodeIds deleted )
    {
        this.result = result;
        this.deleted = deleted;
    }

    @Override
    public void serialize( final MapGenerator gen )
    {
        addSuccessful( gen );
        addFailed( gen );
        addDeleted( gen );
    }

    private void addDeleted( final MapGenerator gen )
    {
        gen.array( "deleted" );

        for ( final NodeId entry : deleted )
        {
            gen.value( entry );
        }

        gen.end();
    }

    private void addFailed( final MapGenerator gen )
    {
        gen.array( "failed" );

        final ImmutableSet<PushNodesResult.Failed> failed = this.result.getFailed();

        for ( final PushNodesResult.Failed entry : failed )
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
        addNodes( gen, result.getSuccessful() );
        gen.end();
    }

    private void addNodes( final MapGenerator gen, final NodeBranchEntries successes )
    {
        for ( final NodeBranchEntry success : successes )
        {
            gen.value( success.getNodeId() );
        }
    }
}
