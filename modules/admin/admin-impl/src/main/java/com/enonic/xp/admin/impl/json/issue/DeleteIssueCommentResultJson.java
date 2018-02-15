package com.enonic.xp.admin.impl.json.issue;

import java.util.List;
import java.util.stream.Collectors;

import com.enonic.xp.issue.DeleteIssueCommentResult;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;

public class DeleteIssueCommentResultJson
{
    public final NodeIds ids;

    public DeleteIssueCommentResultJson( final DeleteIssueCommentResult result )
    {
        this.ids = result.getIds();
    }

    public List<String> getIds()
    {
        return ids.stream().map( NodeId::toString ).collect( Collectors.toList() );
    }
}
