package com.enonic.xp.admin.impl.json.issue;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.enonic.xp.issue.Issue;

public class IssuesJson
{

    private final List<IssueJson> issues;

    public IssuesJson()
    {
        this.issues = new ArrayList<>();
    }

    public IssuesJson( final List<Issue> issues )
    {
        this.issues = issues.stream().map( IssueJson::new ).collect( Collectors.toList() );
    }

    public void addIssue( final Issue issue )
    {
        this.issues.add( new IssueJson( issue ) );
    }

    public void addIssues( final List<Issue> issues )
    {
        this.issues.addAll( issues.stream().map( IssueJson::new ).collect( Collectors.toList() ) );
    }

    public List<IssueJson> getIssues()
    {
        return issues;
    }
}
