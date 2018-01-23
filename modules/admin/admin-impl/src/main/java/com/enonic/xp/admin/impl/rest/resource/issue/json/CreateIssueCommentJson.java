package com.enonic.xp.admin.impl.rest.resource.issue.json;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.issue.IssueId;
import com.enonic.xp.security.PrincipalKey;

public class CreateIssueCommentJson
{
    public final IssueId issueId;

    public final String text;

    public final PrincipalKey creator;

    @JsonCreator
    public CreateIssueCommentJson( @JsonProperty("issue") final String issueId, @JsonProperty("text") final String text,
                                   @JsonProperty("creator") final String creator )
    {
        this.text = text;
        this.creator = PrincipalKey.from( creator );
        this.issueId = IssueId.from( issueId );
    }

}
