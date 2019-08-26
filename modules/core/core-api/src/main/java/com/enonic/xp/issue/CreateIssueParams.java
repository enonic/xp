package com.enonic.xp.issue;

import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.security.PrincipalKeys;

public class CreateIssueParams
{
    private final IssueId id;

    private final String title;

    private final String description;

    private final IssueStatus issueStatus;

    private final PrincipalKeys approverIds;

    private final PublishRequest publishRequest;

    private final IssueType issueType;

    protected CreateIssueParams( Builder builder )
    {
        this.id = IssueId.create();
        this.title = builder.title;
        this.description = builder.description;
        this.issueStatus = builder.issueStatus;
        this.approverIds = builder.approverIds;
        this.publishRequest = builder.publishRequest;
        this.issueType = builder.issueType;
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

    public static Builder create()
    {
        return new Builder();
    }

    public static Builder create( CreateIssueParams params )
    {
        return new Builder( params );
    }

    public static class Builder<B extends Builder>
    {

        private String title;

        private String description;

        private IssueStatus issueStatus;

        private PrincipalKeys approverIds;

        private PublishRequest publishRequest;

        protected IssueType issueType;

        protected PropertyTree data;

        protected Builder()
        {
            this.issueStatus = IssueStatus.OPEN;
            this.approverIds = PrincipalKeys.empty();
            this.issueType = IssueType.STANDARD;
            this.data = new PropertyTree();
        }

        public Builder( CreateIssueParams params )
        {
            this.title = params.title;
            this.description = params.description;
            this.issueStatus = params.issueStatus;
            this.approverIds = params.approverIds;
            this.publishRequest = params.publishRequest;
            this.issueType = params.issueType;
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

        public B setApproverIds( final PrincipalKeys approverIds )
        {
            this.approverIds = approverIds;
            return (B) this;
        }

        public B setPublishRequest( final PublishRequest publishRequest )
        {
            this.publishRequest = publishRequest;
            return (B) this;
        }

        public CreateIssueParams build()
        {
            return new CreateIssueParams( this );
        }
    }
}
