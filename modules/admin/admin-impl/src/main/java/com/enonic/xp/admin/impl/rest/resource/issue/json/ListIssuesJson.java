package com.enonic.xp.admin.impl.rest.resource.issue.json;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.issue.IssueStatus;

public final class ListIssuesJson
{
    private final IssueStatus status;

    private final boolean assignedToMe;

    private final boolean createdByMe;

    private final boolean resolveAssignees;

    private final Integer fromParam;

    private final Integer sizeParam;

    @JsonCreator
    public ListIssuesJson( @JsonProperty("type") final String type, @JsonProperty("assignedToMe") final boolean assignedToMe,
                           @JsonProperty("createdByMe") final boolean createdByMe,
                           @JsonProperty("resolveAssignees") final boolean resolveAssignees, @JsonProperty("from") final Integer fromParam,
                           @JsonProperty("size") final Integer sizeParam )
    {
        this.status = parseIssueStatus( type );
        this.assignedToMe = assignedToMe;
        this.createdByMe = createdByMe;
        this.resolveAssignees = resolveAssignees;
        this.fromParam = fromParam;
        this.sizeParam = sizeParam;
    }

    private IssueStatus parseIssueStatus( final String type )
    {
        if ( type == null )
        {
            return null;
        }

        if ( type.equalsIgnoreCase( "CLOSED" ) )
        {
            return IssueStatus.Closed;
        }

        if ( type.equalsIgnoreCase( "OPEN" ) )
        {
            return IssueStatus.Open;
        }

        return null;
    }

    public IssueStatus getStatus()
    {
        return status;
    }

    public boolean isAssignedToMe()
    {
        return assignedToMe;
    }

    public boolean isCreatedByMe()
    {
        return createdByMe;
    }

    public boolean isResolveAssignees()
    {
        return resolveAssignees;
    }

    public Integer getFromParam()
    {
        return fromParam;
    }

    public Integer getSizeParam()
    {
        return sizeParam;
    }
}
