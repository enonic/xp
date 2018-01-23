package com.enonic.xp.admin.impl.rest.resource.issue.json;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.issue.IssueId;
import com.enonic.xp.security.PrincipalKey;

public final class ListIssueCommentsJson
{
    private final int from;

    private final int size;

    private final PrincipalKey creator;

    private final IssueId issue;

    private final boolean count;

    @JsonCreator
    public ListIssueCommentsJson( @JsonProperty("issue") final String issueParam, @JsonProperty("creator") final String creatorParam,
                                  @JsonProperty("from") final int fromParam, @JsonProperty("size") final int sizeParam,
                                  @JsonProperty(value = "count") final boolean countParam )
    {
        this.issue = IssueId.from( issueParam );
        this.creator = creatorParam != null ? PrincipalKey.from( creatorParam ) : null;
        this.from = fromParam;
        this.size = sizeParam;
        this.count = countParam;
    }

    public int getFrom()
    {
        return from;
    }

    public int getSize()
    {
        return size;
    }

    public PrincipalKey getCreator()
    {
        return creator;
    }

    public IssueId getIssue()
    {
        return issue;
    }

    public boolean isCount()
    {
        return count;
    }
}
