package com.enonic.xp.issue;

import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.security.PrincipalKeys;

public final class CreateIssueParams
{
    private final IssueId id;

    private final String title;

    private final String description;

    private final IssueStatus issueStatus;

    private final PrincipalKeys approverIds;

    private final PublishRequest publishRequest;

    private final IssueType issueType;

    private final PropertyTree data;

    private CreateIssueParams( Builder builder )
    {
        this.id = IssueId.create();
        this.title = builder.title;
        this.description = builder.description;
        this.issueStatus = builder.issueStatus;
        this.approverIds = builder.approverIds;
        this.publishRequest = builder.publishRequest;
        this.issueType = builder.issueType;
        this.data = builder.data;
    }

    public IssueId getId()
    {
        return id;
    }

    public String getTitle()
    {
        return title;
    }

    public String getDescription()
    {
        return description;
    }

    public IssueStatus getStatus()
    {
        return issueStatus;
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

    public PropertyTree getData()
    {
        return data;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {

        private String title;

        private String description;

        private IssueStatus issueStatus;

        private PrincipalKeys approverIds;

        private PublishRequest publishRequest;

        private IssueType issueType;

        private PropertyTree data;

        private Builder()
        {
            this.issueStatus = IssueStatus.OPEN;
            this.approverIds = PrincipalKeys.empty();
            this.issueType = IssueType.STANDARD;
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

        public Builder setApproverIds( final PrincipalKeys approverIds )
        {
            this.approverIds = approverIds;
            return this;
        }

        public Builder setPublishRequest( final PublishRequest publishRequest )
        {
            this.publishRequest = publishRequest;
            return this;
        }

        public Builder type( final IssueType issueType )
        {
            this.issueType = issueType;
            return this;
        }

        public Builder data( final PropertyTree data )
        {
            this.data = data;
            return this;
        }

        public CreateIssueParams build()
        {
            return new CreateIssueParams( this );
        }
    }
}
