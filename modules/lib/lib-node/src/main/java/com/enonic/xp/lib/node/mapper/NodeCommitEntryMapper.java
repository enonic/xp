package com.enonic.xp.lib.node.mapper;

import com.enonic.xp.node.NodeCommitEntry;
import com.enonic.xp.script.serializer.MapGenerator;
import com.enonic.xp.script.serializer.MapSerializable;

public class NodeCommitEntryMapper
    implements MapSerializable
{
    private final NodeCommitEntry commitEntry;


    public NodeCommitEntryMapper( final NodeCommitEntry commitEntry )
    {
        this.commitEntry = commitEntry;
    }

    @Override
    public void serialize( final MapGenerator gen )
    {
        gen.value( "id", commitEntry.getNodeCommitId() );
        gen.value( "message", commitEntry.getMessage() );
        gen.value( "committer", commitEntry.getCommitter() );
        gen.value( "timestamp", commitEntry.getTimestamp() );
    }
}