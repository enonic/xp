package com.enonic.xp.admin.impl.rest.resource.commit;

import java.time.Instant;

import com.enonic.xp.node.NodeCommitEntry;

public class GetCommitResultJson
{
    private final NodeCommitEntry nodeCommitEntry;

    public GetCommitResultJson( final NodeCommitEntry nodeCommitEntry )
    {
        this.nodeCommitEntry = nodeCommitEntry;
    }

    public String getNodeCommitId()
    {
        return nodeCommitEntry.getNodeCommitId().toString();
    }

    public String getMessage()
    {
        return nodeCommitEntry.getMessage();
    }

    public Instant getInstant()
    {
        return nodeCommitEntry.getTimestamp();
    }

    public String getCommiter()
    {
        return nodeCommitEntry.getCommitter().toString();
    }
}
