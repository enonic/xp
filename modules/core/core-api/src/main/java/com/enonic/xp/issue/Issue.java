package com.enonic.xp.issue;

import java.time.Instant;
import java.util.Set;

import com.google.common.collect.Sets;

import com.enonic.xp.name.NamePrettyfier;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalKeys;

public class Issue
{
    private final IssueId id;

    private final String title;

    private final IssueName name;

    private final IssuePath issuePath;

    private final String description;

    private final Instant createdTime;

    private final Instant modifiedTime;

    private final IssueStatus issueStatus;

    private final PrincipalKey creator;

    private final PrincipalKey modifier;

    private final PrincipalKeys approverIds;

    private final PublishRequest publishRequest;

    private Issue( Builder builder )
    {
        this.id = builder.id == null ? IssueId.create() : builder.id;
        this.title = builder.title;
        this.name = builder.name == null ? IssueName.from( NamePrettyfier.create( builder.title ) ) : builder.name;
        this.issuePath = IssuePath.from( this.name );
        this.description = builder.description;
        this.createdTime = builder.createdTime;
        this.modifiedTime = builder.modifiedTime;
        this.issueStatus = builder.issueStatus;
        this.creator = builder.creator;
        this.modifier = builder.modifier;
        this.approverIds = PrincipalKeys.from( builder.approverIds );
        this.publishRequest = builder.publishRequest;
    }

    public IssueId getId()
    {
        return id;
    }

    public String getTitle()
    {
        return title;
    }

    public IssueName getName()
    {
        return name;
    }

    public IssuePath getPath()
    {
        return issuePath;
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

    public static Builder create()
    {
        return new Builder();
    }

    public static Builder create( final Issue source ) {
        return new Builder(source);
    }

    public static class Builder
    {
        private IssueId id;

        private IssueName name;

        private String title;

        private String description;

        private Instant createdTime;

        private Instant modifiedTime;

        private IssueStatus issueStatus;

        private PrincipalKey creator;

        private PrincipalKey modifier;

        private Set<PrincipalKey> approverIds;

        private PublishRequest publishRequest;

        public Builder()
        {
            this.approverIds = Sets.newHashSet();
            this.issueStatus = IssueStatus.Open;
        }

        protected Builder( final Issue source )
        {
            this.id = source.id;
            this.title = source.title;
            this.name = source.name;
            this.description = source.description;
            this.createdTime = source.createdTime;
            this.modifiedTime = source.modifiedTime;
            this.issueStatus = source.issueStatus;
            this.creator = source.creator;
            this.modifier = source.modifier;
            this.approverIds = source.approverIds != null ? source.approverIds.getSet() : Sets.newHashSet();
            this.publishRequest = source.publishRequest;
        }

        public Builder id( final IssueId id )
        {
            this.id = id;
            return this;
        }

        public Builder name( final IssueName name )
        {
            this.name = name;
            return this;
        }

        public Builder title( final String title )
        {
            this.title = title;
            return this;
        }

        public Builder description( final String description )
        {
            this.description = description;
            return this;
        }

        public Builder createdTime( final Instant createdTime )
        {
            this.createdTime = createdTime;
            return this;
        }

        public Builder modifiedTime( final Instant modifiedTime )
        {
            this.modifiedTime = modifiedTime;
            return this;
        }

        public Builder status( final IssueStatus issueStatus )
        {
            this.issueStatus = issueStatus;
            return this;
        }

        public Builder creator( final PrincipalKey creator )
        {
            this.creator = creator;
            return this;
        }

        public Builder modifier( final PrincipalKey modifier )
        {
            this.modifier = modifier;
            return this;
        }

        public Builder addApproverId( final PrincipalKey approverId )
        {
            this.approverIds.add( approverId );
            return this;
        }

        public Builder addApproverIds( final PrincipalKeys approverIds )
        {
            this.approverIds.addAll( approverIds.getSet() );
            return this;
        }

        public Builder setPublishRequest( final PublishRequest publishRequest )
        {
            this.publishRequest = publishRequest;
            return this;
        }

        public Issue build()
        {
            return new Issue( this );
        }
    }
}
