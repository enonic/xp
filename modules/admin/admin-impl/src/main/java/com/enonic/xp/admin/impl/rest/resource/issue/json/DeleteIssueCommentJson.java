package com.enonic.xp.admin.impl.rest.resource.issue.json;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.issue.DeleteIssueCommentParams;
import com.enonic.xp.issue.IssueId;
import com.enonic.xp.node.NodeName;

public final class DeleteIssueCommentJson
{
    private final NodeName comment;

    private final IssueId issue;

    @JsonCreator
    public DeleteIssueCommentJson( @JsonProperty("issue") final String issueParam, @JsonProperty("comment") final String comment )
    {
        this.issue = IssueId.from( issueParam );
        this.comment = NodeName.from( comment );
    }

    public IssueId getIssue()
    {
        return issue;
    }

    public NodeName getComment()
    {
        return comment;
    }

    public DeleteIssueCommentParams getDeleteIssueCommentParams()
    {
        return DeleteIssueCommentParams.create().issue( issue ).comment( comment ).build();
    }
}
