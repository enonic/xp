package com.enonic.xp.admin.impl.rest.resource.issue.json;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.issue.IssueId;

public class GetIssuesJson
{
    private final List<IssueId> issueIds;

    @JsonCreator
    public GetIssuesJson( @JsonProperty("ids") final List<String> ids )
    {

        this.issueIds = new ArrayList<>();
        for ( String id : ids )
        {
            issueIds.add( IssueId.from( id ) );
        }
    }

    @JsonIgnore
    public List<IssueId> getIssueIds()
    {
        return issueIds;
    }
}
