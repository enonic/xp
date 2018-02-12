package com.enonic.xp.admin.impl.rest.resource.issue.json;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.issue.IssueId;
import com.enonic.xp.issue.UpdateIssueCommentParams;
import com.enonic.xp.node.NodeName;

public class UpdateIssueCommentJson
{
    public final IssueId issueId;

    public final NodeName commentName;

    public final String text;

    @JsonCreator
    public UpdateIssueCommentJson( @JsonProperty("issue") final String issueId, @JsonProperty("comment") final String commentName,
                                   @JsonProperty("text") final String text )
    {
        this.text = text;
        this.issueId = IssueId.from( issueId );
        this.commentName = NodeName.from( commentName );
    }

    public UpdateIssueCommentParams toUpdateIssueCommentParams()
    {
        return UpdateIssueCommentParams.create().issue( issueId ).comment( commentName ).text( text ).build();
    }

}
