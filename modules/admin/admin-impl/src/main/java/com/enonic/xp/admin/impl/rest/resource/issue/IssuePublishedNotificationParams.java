package com.enonic.xp.admin.impl.rest.resource.issue;

import com.enonic.xp.security.User;

public class IssuePublishedNotificationParams
    extends IssueNotificationParams
{
    private User publisher;

    public IssuePublishedNotificationParams( final Builder builder )
    {
        super( builder );
        this.publisher = builder.publisher;
    }

    public User getPublisher()
    {
        return publisher;
    }

    public static Builder create( final User publisher, final IssueNotificationParams source )
    {
        return new Builder( publisher, source );
    }

    public static class Builder
        extends IssueNotificationParams.Builder<Builder>
    {
        private User publisher;

        private Builder( final User publisher, final IssueNotificationParams source )
        {
            super( source );
            this.publisher = publisher;
        }

        public IssuePublishedNotificationParams build()
        {
            return new IssuePublishedNotificationParams( this );
        }
    }
}
