package com.enonic.xp.admin.impl.json.issue;

import java.util.List;
import java.util.stream.Collectors;

import com.enonic.xp.issue.DeleteIssueCommentResult;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodePath;

public class DeleteIssueCommentResultJson
{
    public final NodePath path;

    public final NodeIds ids;

    public DeleteIssueCommentResultJson( final DeleteIssueCommentResult result )
    {
        this.path = result.getPath();
        this.ids = result.getIds();
    }

    public String getPath()
    {
        return path != null ? path.toString() : null;
    }

    public List<String> getIds()
    {
        return ids.stream().map( NodeId::toString ).collect( Collectors.toList() );
    }

}
