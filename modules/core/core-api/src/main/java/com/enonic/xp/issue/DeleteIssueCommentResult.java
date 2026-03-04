package com.enonic.xp.issue;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.node.NodeIds;

@PublicApi
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
