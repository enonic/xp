package com.enonic.xp.issue;


import com.enonic.xp.annotation.PublicApi;

@PublicApi
public interface IssueService
{
    Issue create( CreateIssueParams params );

    Issue update( UpdateIssueParams params );

    Issue getIssue( IssueId id );

    FindIssuesResult findIssues( IssueQuery query );

    IssueComment createComment( CreateIssueCommentParams params );

    FindIssueCommentsResult findComments( IssueCommentQuery query );

    DeleteIssueCommentResult deleteComment( DeleteIssueCommentParams params );

    IssueComment updateComment( UpdateIssueCommentParams params );

}
