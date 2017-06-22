package com.enonic.xp.admin.impl.rest.resource.issue.json;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.issue.FindIssuesParams;
import com.enonic.xp.issue.IssueStatus;

public final class ListIssuesJson
{
    private final FindIssuesParams findIssuesParams;

    private final boolean resolveAssignees;

    @JsonCreator
    public ListIssuesJson( @JsonProperty("type") final String type, @JsonProperty("assignedToMe") final boolean assignedToMe,
                           @JsonProperty("createdByMe") final boolean createdByMe,
                           @JsonProperty("resolveAssignees") final boolean resolveAssignees, @JsonProperty("from") final Integer fromParam,
                           @JsonProperty("size") final Integer sizeParam )
    {
        this.findIssuesParams =
            FindIssuesParams.create().status( parseIssueStatus( type ) ).assignedToMe( assignedToMe ).createdByMe( createdByMe ).from(
                fromParam ).size( sizeParam ).build();

        this.resolveAssignees = resolveAssignees;
    }

    private IssueStatus parseIssueStatus( final String type )
    {
        if ( type == null )
        {
            return null;
        }

        if ( type.equalsIgnoreCase( "CLOSED" ) )
        {
            return IssueStatus.CLOSED;
        }

        if ( type.equalsIgnoreCase( "OPEN" ) )
        {
            return IssueStatus.OPEN;
        }

        return null;
    }

    public FindIssuesParams getFindIssuesParams()
    {
        return findIssuesParams;
    }

    public boolean isResolveAssignees()
    {
        return resolveAssignees;
    }
}
