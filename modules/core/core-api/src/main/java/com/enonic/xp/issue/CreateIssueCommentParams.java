package com.enonic.xp.issue;

import java.time.Instant;

import com.enonic.xp.security.PrincipalKey;

public final class CreateIssueCommentParams
{
    private final IssueName issueName;

    private final String text;

    private final PrincipalKey creator;

    private final String creatorDisplayName;

    private final Instant created;

    private CreateIssueCommentParams( Builder builder )
    {
        this.issueName = builder.issueName;
        this.text = builder.text;
        this.creator = builder.creator;
        this.creatorDisplayName = builder.creatorDisplayName;
        this.created = builder.created;
    }

    public IssueName getIssueName()
    {
        return issueName;
    }

    public String getText()
    {
        return text;
    }

    public PrincipalKey getCreator()
    {
        return creator;
    }

    public String getCreatorDisplayName()
    {
        return creatorDisplayName;
    }

    public Instant getCreated()
    {
        return created;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        private IssueName issueName;

        private String text;

        private PrincipalKey creator;

        private String creatorDisplayName;

        private Instant created;

        private Builder()
        {
            this.created = Instant.now();
        }

        public Builder issueName( final IssueName issueName )
        {
            this.issueName = issueName;
            return this;
        }

        public Builder text( final String text )
        {
            this.text = text;
            return this;
        }

        public Builder creator( final PrincipalKey creator )
        {
            this.creator = creator;
            return this;
        }

        public Builder creatorDisplayName( final String creatorDisplayName )
        {
            this.creatorDisplayName = creatorDisplayName;
            return this;
        }

        public Builder created( final Instant created )
        {
            this.created = created;
            return this;
        }

        public CreateIssueCommentParams build()
        {
            return new CreateIssueCommentParams( this );
        }
    }
}
