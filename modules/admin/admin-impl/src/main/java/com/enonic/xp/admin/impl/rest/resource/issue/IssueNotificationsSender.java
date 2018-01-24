package com.enonic.xp.admin.impl.rest.resource.issue;

import java.util.List;

import com.enonic.xp.issue.Issue;
import com.enonic.xp.issue.IssueComment;

public interface IssueNotificationsSender
{
    void notifyIssueCreated( final Issue issue, final String url );

    void notifyIssuePublished( final Issue issue, List<IssueComment> comments, final String url );

    void notifyIssueUpdated( final Issue issue, List<IssueComment> comments, final String url );

    void notifyIssueCommented( final Issue issue, List<IssueComment> comments, final String url );

}
