package com.enonic.xp.admin.impl.json.issue;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.enonic.xp.admin.impl.rest.resource.issue.IssueListMetaData;
import com.enonic.xp.issue.Issue;
import com.enonic.xp.security.User;

public class IssueListJson
{
    private final List<IssueResolvedAssigneesJson> issues;

    private final IssueListMetaDataJson metadata;

    public IssueListJson( final List<Issue> issues, final IssueListMetaData metadata )
    {
        this.issues = issues.stream().map( IssueResolvedAssigneesJson::new ).collect( Collectors.toList() );
        this.metadata = new IssueListMetaDataJson( metadata );
    }

    public IssueListJson( final Map<Issue, List<User>> issuesWithAssignees, final IssueListMetaData metadata )
    {
        this.issues =
            issuesWithAssignees.entrySet().stream().map( e -> new IssueResolvedAssigneesJson( e.getKey(), e.getValue() ) ).collect(
                Collectors.toList() );
        this.metadata = new IssueListMetaDataJson( metadata );
    }

    public List<IssueResolvedAssigneesJson> getIssues()
    {
        return issues;
    }

    public IssueListMetaDataJson getMetadata()
    {
        return metadata;
    }
}
