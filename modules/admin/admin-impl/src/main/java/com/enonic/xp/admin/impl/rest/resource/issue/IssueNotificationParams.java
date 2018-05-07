package com.enonic.xp.admin.impl.rest.resource.issue;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.enonic.xp.content.CompareContentResults;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.Contents;
import com.enonic.xp.issue.Issue;
import com.enonic.xp.issue.IssueComment;
import com.enonic.xp.security.User;

public class IssueNotificationParams
{
    private final Issue issue;

    private final User creator;

    private final List<User> approvers;

    private final Contents items;

    private final String url;

    private final Map<ContentId, String> icons;

    private final CompareContentResults compareResults;

    private final List<IssueComment> comments;

    public IssueNotificationParams( final Builder builder )
    {
        this.issue = builder.issue;
        this.url = builder.url;
        this.icons = builder.icons;
        this.creator = builder.creator;
        this.approvers = builder.approvers;
        this.items = builder.items;
        this.compareResults = builder.compareResults;
        this.comments = builder.comments != null ? builder.comments : Collections.emptyList();
    }

    public Issue getIssue()
    {
        return issue;
    }

    public User getCreator()
    {
        return creator;
    }

    public List<User> getApprovers()
    {
        return approvers;
    }

    public Contents getItems()
    {
        return items;
    }

    public String getUrl()
    {
        return url;
    }

    public Map<ContentId, String> getIcons()
    {
        return icons;
    }

    public CompareContentResults getCompareResults()
    {
        return compareResults;
    }

    public boolean hasValidCreator()
    {
        return creator != null && creator.getEmail() != null;
    }

    public List<IssueComment> getComments()
    {
        return this.comments;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder<B extends Builder>
    {
        private Issue issue;

        private User creator;

        private List<User> approvers;

        private Contents items;

        private String url;

        private Map<ContentId, String> icons;

        private CompareContentResults compareResults;

        private List<IssueComment> comments;

        protected Builder()
        {
        }

        protected Builder( final IssueNotificationParams source )
        {
            this.issue = source.issue;
            this.creator = source.creator;
            this.approvers = source.approvers;
            this.items = source.items;
            this.url = source.url;
            this.icons = source.icons;
            this.compareResults = source.compareResults;
            this.comments = source.comments;
        }

        public B issue( final Issue issue )
        {
            this.issue = issue;
            return (B) this;
        }

        public B creator( final User creator )
        {
            this.creator = creator;
            return (B) this;
        }

        public B approvers( final List<User> approvers )
        {
            this.approvers = approvers;
            return (B) this;
        }

        public B items( final Contents items )
        {
            this.items = items;
            return (B) this;
        }

        public B url( final String url )
        {
            this.url = url;
            return (B) this;
        }

        public B icons( final Map<ContentId, String> icons )
        {
            this.icons = icons;
            return (B) this;
        }

        public B compareResults( final CompareContentResults compareResults )
        {
            this.compareResults = compareResults;
            return (B) this;
        }

        public B comments( final List<IssueComment> comments )
        {
            this.comments = comments;
            return (B) this;
        }

        public IssueNotificationParams build()
        {
            return new IssueNotificationParams( this );
        }
    }
}
