package com.enonic.xp.issue;

import java.time.Instant;

import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalKeys;

public class Issue
{
    private final IssueId id;

    private final long index;

    private final String title;

    private final IssueName name;

    private final String description;

    private final Instant createdTime;

    private final Instant modifiedTime;

    private final IssueStatus issueStatus;

    private final PrincipalKey creator;

    private final PrincipalKey modifier;

    private final PrincipalKeys approverIds;

    private final PublishRequest publishRequest;

    private final IssueType issueType;

    protected Issue( Builder<?> builder )
    {
        this.id = builder.id == null ? IssueId.create() : builder.id;
        this.index = builder.index;
        this.title = builder.title;
        this.name = builder.name == null ? IssueName.from( this.id.toString() ) : builder.name;
        this.description = builder.description;
        this.createdTime = builder.createdTime;
        this.modifiedTime = builder.modifiedTime;
        this.issueStatus = builder.issueStatus;
        this.creator = builder.creator;
        this.modifier = builder.modifier;
        this.approverIds = builder.approverIds.build();
        this.publishRequest = builder.publishRequest;
        this.issueType = builder.issueType;
    }

    public IssueId getId()
    {
        return id;
    }

    public long getIndex()
    {
        return index;
    }

    public String getTitle()
    {
        return title;
    }

    public IssueName getName()
    {
        return name;
    }

    public String getDescription()
    {
        return description;
    }

    public Instant getCreatedTime()
    {
        return createdTime;
    }

    public Instant getModifiedTime()
    {
        return modifiedTime;
    }

    public IssueStatus getStatus()
    {
        return issueStatus;
    }

    public PrincipalKey getCreator()
    {
        return creator;
    }

    public PrincipalKey getModifier()
    {
        return modifier;
    }

    public PrincipalKeys getApproverIds()
    {
        return approverIds;
    }

    public PublishRequest getPublishRequest()
    {
        return publishRequest;
    }

    public IssueType getIssueType()
    {
        return issueType;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static Builder create( final Issue source )
    {
        return new Builder( source );
    }

    public static class Builder<B extends Builder>
    {
        private IssueId id;

        private long index;

        private IssueName name;

        private String title;

        private String description;

        private Instant createdTime;

        private Instant modifiedTime;

        private IssueStatus issueStatus;

        private PrincipalKey creator;

        private PrincipalKey modifier;

        private final PrincipalKeys.Builder approverIds;

        private PublishRequest publishRequest;

        protected IssueType issueType;

        public Builder()
        {
            this.approverIds = PrincipalKeys.create();
            this.issueStatus = IssueStatus.OPEN;
            this.issueType = IssueType.STANDARD;
        }

        protected Builder( final Issue source )
        {
            this.id = source.id;
            this.index = source.index;
            this.title = source.title;
            this.name = source.name;
            this.description = source.description;
            this.createdTime = source.createdTime;
            this.modifiedTime = source.modifiedTime;
            this.issueStatus = source.issueStatus;
            this.creator = source.creator;
            this.modifier = source.modifier;
            this.approverIds = PrincipalKeys.create().addAll( source.approverIds );
            this.publishRequest = source.publishRequest;
            this.issueType = source.issueType;
        }

        public B id( final IssueId id )
        {
            this.id = id;
            return (B) this;
        }

        public B index( final long index )
        {
            this.index = index;
            return (B) this;
        }

        public B name( final IssueName name )
        {
            this.name = name;
            return (B) this;
        }

        public B title( final String title )
        {
            this.title = title;
            return (B) this;
        }

        public B description( final String description )
        {
            this.description = description;
            return (B) this;
        }

        public B createdTime( final Instant createdTime )
        {
            this.createdTime = createdTime;
            return (B) this;
        }

        public B modifiedTime( final Instant modifiedTime )
        {
            this.modifiedTime = modifiedTime;
            return (B) this;
        }

        public B status( final IssueStatus issueStatus )
        {
            this.issueStatus = issueStatus;
            return (B) this;
        }

        public B creator( final PrincipalKey creator )
        {
            this.creator = creator;
            return (B) this;
        }

        public B modifier( final PrincipalKey modifier )
        {
            this.modifier = modifier;
            return (B) this;
        }

        public B addApproverId( final PrincipalKey approverId )
        {
            this.approverIds.add( approverId );
            return (B) this;
        }

        public B addApproverIds( final PrincipalKeys approverIds )
        {
            this.approverIds.addAll( approverIds );
            return (B) this;
        }

        public B setPublishRequest( final PublishRequest publishRequest )
        {
            this.publishRequest = publishRequest;
            return (B) this;
        }

        public Issue build()
        {
            return new Issue( this );
        }
    }
}
