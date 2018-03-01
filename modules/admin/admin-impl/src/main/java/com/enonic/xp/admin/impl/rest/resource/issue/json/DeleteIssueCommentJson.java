package com.enonic.xp.admin.impl.rest.resource.issue.json;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.node.NodeId;

public final class DeleteIssueCommentJson
{
    private final NodeId comment;

    @JsonCreator
    public DeleteIssueCommentJson( @JsonProperty("comment") final String comment )
    {
        this.comment = NodeId.from( comment );
    }

    public NodeId getComment()
    {
        return comment;
    }
}
