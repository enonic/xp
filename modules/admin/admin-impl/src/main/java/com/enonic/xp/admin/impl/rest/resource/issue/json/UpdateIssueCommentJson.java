package com.enonic.xp.admin.impl.rest.resource.issue.json;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.node.NodeId;

public class UpdateIssueCommentJson
{
    private final NodeId comment;

    private final String text;

    @JsonCreator
    public UpdateIssueCommentJson( @JsonProperty("comment") final String comment, @JsonProperty("text") final String text )
    {
        this.text = text;
        this.comment = NodeId.from( comment );
    }

    public NodeId getComment()
    {
        return comment;
    }

    public String getText()
    {
        return text;
    }
}
