package com.enonic.xp.admin.impl.rest.resource.issue;

public interface IssueNotificationsSender
{
    void notifyIssueCreated( IssueNotificationParams params );

    void notifyIssuePublished( IssuePublishedNotificationParams params );

    void notifyIssueUpdated( IssueUpdatedNotificationParams params );

    void notifyIssueCommented( IssueCommentedNotificationParams params );
}
