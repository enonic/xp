package com.enonic.xp.issue;

import java.time.Instant;

import com.enonic.xp.node.NodeName;
import com.enonic.xp.security.PrincipalKey;

public class IssueComment
{
    private NodeName name;

    private PrincipalKey creator;

    private String creatorDisplayName;

    private Instant created;

    private String text;

    private IssueComment( Builder builder )
    {
        this.name = builder.name;
        this.creator = builder.creator;
        this.creatorDisplayName = builder.creatorDisplayName;
        this.created = builder.created;
        this.text = builder.text;
    }

    public NodeName getName()
    {
        return name;
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

    public String getText()
    {
        return text;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        private NodeName name;

        private String text;

        private PrincipalKey creator;

        private String creatorDisplayName;

        private Instant created;

        private Builder()
        {
            this.created = Instant.now();
        }

        public Builder name( final NodeName name )
        {
            this.name = name;
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

        public IssueComment build()
        {
            return new IssueComment( this );
        }
    }
}
