package com.enonic.xp.admin.impl.rest.resource.issue;

import com.enonic.xp.issue.Issue;

public interface IssueNotificationsSender
{
    void notifyIssueCreated( final Issue issue, final String url );
}
