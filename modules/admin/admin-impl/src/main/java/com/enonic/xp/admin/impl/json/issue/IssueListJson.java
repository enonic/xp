package com.enonic.xp.admin.impl.json.issue;

import java.util.List;
import java.util.stream.Collectors;

import com.enonic.xp.issue.Issue;
import com.enonic.xp.issue.IssueListMetaData;

public class IssueListJson
{
    private final List<IssueJson> issues;

    private final IssueListMetaDataJson metadata;

    public IssueListJson( final List<Issue> issues, final IssueListMetaData metadata )
    {
        this.issues = issues.stream().map( IssueJson::new ).collect( Collectors.toList() );
        this.metadata = new IssueListMetaDataJson( metadata );
    }

    public List<IssueJson> getIssues()
    {
        return issues;
    }

    public IssueListMetaDataJson getMetadata()
    {
        return metadata;
    }
}
