package com.enonic.xp.admin.impl.rest.resource.issue.json;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.issue.IssueId;
import com.enonic.xp.security.PrincipalKey;

public class CommentIssueJson
{
    public final IssueId issueId;

    public final String text;

    public final PrincipalKey creatorKey;

    public final String creatorDisplayName;

    @JsonCreator
    public CommentIssueJson( @JsonProperty("issueId") final String issueId, @JsonProperty("text") final String text,
                             @JsonProperty("creatorKey") final String creatorKey,
                             @JsonProperty("creatorDisplayName") final String creatorDisplayName )
    {
        this.text = text;
        this.creatorKey = PrincipalKey.from( creatorKey );
        this.creatorDisplayName = creatorDisplayName;
        this.issueId = IssueId.from( issueId );
    }

}
