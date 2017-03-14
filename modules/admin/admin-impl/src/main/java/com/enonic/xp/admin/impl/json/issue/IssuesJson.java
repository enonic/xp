package com.enonic.xp.admin.impl.json.issue;

import java.util.ArrayList;
import java.util.List;

import com.enonic.xp.issue.Issue;

public class IssuesJson
{

    private List<Issue> issues;

    public IssuesJson()
    {
        this.issues = new ArrayList<>();
    }

    public void addIssue( Issue issue )
    {
        this.issues.add( issue );
    }

    public List<Issue> getIssues()
    {
        return issues;
    }
}
