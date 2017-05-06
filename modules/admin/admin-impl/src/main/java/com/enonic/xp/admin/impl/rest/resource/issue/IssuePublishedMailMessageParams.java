package com.enonic.xp.admin.impl.rest.resource.issue;

import com.enonic.xp.security.User;

public class IssuePublishedMailMessageParams
    extends IssueMailMessageParams
{
    private User publisher;

    public IssuePublishedMailMessageParams( final Builder builder )
    {
        super( builder );
        this.publisher = builder.publisher;
    }

    public User getPublisher()
    {
        return publisher;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static Builder create( final User publisher, final IssueMailMessageParams source )
    {
        return new Builder( publisher, source );
    }

    public static class Builder
        extends IssueMailMessageParams.Builder<Builder>
    {
        private User publisher;

        private Builder()
        {
        }

        private Builder( final User publisher, final IssueMailMessageParams source )
        {
            super( source );
            this.publisher = publisher;
        }

        public Builder modifier( final User modifier )
        {
            this.publisher = modifier;
            return this;
        }

        public IssuePublishedMailMessageParams build()
        {
            return new IssuePublishedMailMessageParams( this );
        }
    }
}
