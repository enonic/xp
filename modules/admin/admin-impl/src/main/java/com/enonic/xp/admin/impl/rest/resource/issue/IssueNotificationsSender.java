package com.enonic.xp.admin.impl.rest.resource.issue;

import java.util.List;

import com.enonic.xp.issue.Issue;
import com.enonic.xp.issue.IssueComment;
import com.enonic.xp.security.PrincipalKeys;

public interface IssueNotificationsSender
{
    void notifyIssueCreated( final Issue issue, final List<IssueComment> comments, final PrincipalKeys recipients, final String url );

    void notifyIssuePublished( final Issue issue, final List<IssueComment> comments, final PrincipalKeys recipients, final String url );

    void notifyIssueUpdated( final Issue issue, final List<IssueComment> comments, final PrincipalKeys recipients, final String url );

    void notifyIssueCommented( final Issue issue, final List<IssueComment> comments, final PrincipalKeys recipients, final String url );

}
