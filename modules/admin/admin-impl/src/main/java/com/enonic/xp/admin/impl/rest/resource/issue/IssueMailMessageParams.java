package com.enonic.xp.admin.impl.rest.resource.issue;

import java.util.List;
import java.util.Map;

import com.enonic.xp.content.CompareContentResults;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.Contents;
import com.enonic.xp.issue.Issue;
import com.enonic.xp.security.User;

public class IssueMailMessageParams
{
    private final Issue issue;

    private final User creator;

    private final List<User> approvers;

    private final Contents items;

    private final String url;

    private final Map<ContentId, String> icons;

    private final CompareContentResults compareResults;

    public IssueMailMessageParams( final Builder builder )
    {
        this.issue = builder.issue;
        this.url = builder.url;
        this.icons = builder.icons;
        this.creator = builder.creator;
        this.approvers = builder.approvers;
        this.items = builder.items;
        this.compareResults = builder.compareResults;
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

        protected Builder()
        {
        }

        protected Builder( final IssueMailMessageParams source )
        {
            this.issue = source.issue;
            this.creator = source.creator;
            this.approvers = source.approvers;
            this.items = source.items;
            this.url = source.url;
            this.icons = source.icons;
            this.compareResults = source.compareResults;
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

        public IssueMailMessageParams build()
        {
            return new IssueMailMessageParams( this );
        }
    }
}
