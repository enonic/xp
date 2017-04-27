package com.enonic.xp.issue;


import java.util.List;

import com.google.common.annotations.Beta;

@Beta
public interface IssueService
{
    Issue create( CreateIssueParams params );

    Issue update( UpdateIssueParams params );

    Issue getIssue( IssueId id );

    List<Issue> findIssues( IssueQuery query );

    Long countIssues( IssueQuery query );

}
