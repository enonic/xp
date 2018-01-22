package com.enonic.xp.admin.impl.rest.resource.issue.json;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.issue.IssueName;
import com.enonic.xp.security.PrincipalKey;

public final class ListIssueCommentsJson
{
    private final int from;

    private final int size;

    private final PrincipalKey creator;

    private final IssueName issueName;

    private final boolean count;

    @JsonCreator
    public ListIssueCommentsJson( @JsonProperty("issueName") final String issueParam, @JsonProperty("creator") final String creatorParam,
                                  @JsonProperty("from") final int fromParam, @JsonProperty("size") final int sizeParam,
                                  @JsonProperty(value = "count") final boolean countParam )
    {
        this.issueName = IssueName.from( issueParam );
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

    public IssueName getIssueName()
    {
        return issueName;
    }

    public boolean isCount()
    {
        return count;
    }
}
