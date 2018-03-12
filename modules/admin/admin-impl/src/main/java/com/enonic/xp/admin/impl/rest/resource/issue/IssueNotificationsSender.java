package com.enonic.xp.admin.impl.rest.resource.issue;

public interface IssueNotificationsSender
{
    void notifyIssueCreated( final IssueNotificationParams params );

    void notifyIssuePublished( final IssuePublishedNotificationParams params );

    void notifyIssueUpdated( final IssueUpdatedNotificationParams params );

    void notifyIssueCommented( final IssueCommentedNotificationParams params );

}
