package com.enonic.xp.issue;


import com.google.common.annotations.Beta;

@Beta
public interface IssueService
{
    Issue create( CreateIssueParams params );

    Issue getIssue( IssueId id );
}
