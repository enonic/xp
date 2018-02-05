package com.enonic.xp.issue;

import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodePath;

public final class DeleteIssueCommentResult
{
    private final NodeIds ids;

    private final NodePath path;

    public DeleteIssueCommentResult( NodeIds ids, NodePath path )
    {
        this.ids = ids;
        this.path = path;
    }

    public NodeIds getIds()
    {
        return ids;
    }

    public NodePath getPath()
    {
        return path;
    }

}
