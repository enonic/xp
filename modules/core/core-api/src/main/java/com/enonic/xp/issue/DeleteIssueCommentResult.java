package com.enonic.xp.issue;

import com.enonic.xp.node.NodeIds;

public final class DeleteIssueCommentResult
{
    private final NodeIds ids;

    public DeleteIssueCommentResult( NodeIds ids )
    {
        this.ids = ids;
    }

    public NodeIds getIds()
    {
        return ids;
    }
}
