package com.enonic.xp.admin.impl.json.issue;

import java.util.List;
import java.util.stream.Collectors;

import com.enonic.xp.admin.impl.rest.resource.auth.json.UserJson;
import com.enonic.xp.issue.Issue;
import com.enonic.xp.security.User;

public class IssueResolvedAssigneesJson
{
    private final IssueJson issue;

    private final List<UserJson> assignees;

    public IssueResolvedAssigneesJson( final Issue issue )
    {
        this.issue = new IssueJson( issue );
        this.assignees = null;
    }

    public IssueResolvedAssigneesJson( final Issue issue, List<User> assignees )
    {
        this.issue = new IssueJson( issue );
        this.assignees = assignees.stream().map( UserJson::new ).collect( Collectors.toList() );
    }

    public IssueJson getIssue()
    {
        return issue;
    }

    public List<UserJson> getAssignees()
    {
        return assignees;
    }
}
