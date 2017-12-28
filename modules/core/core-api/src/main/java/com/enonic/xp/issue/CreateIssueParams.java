package com.enonic.xp.issue;

import java.util.List;

import com.enonic.xp.security.PrincipalKeys;

public final class CreateIssueParams
{
    private final IssueId id;

    private final String title;

    private final String description;

    private final IssueStatus issueStatus;

    private final PrincipalKeys approverIds;

    private final List<Comment> comments;

    private final PublishRequest publishRequest;

    private CreateIssueParams( Builder builder )
    {
        this.id = IssueId.create();
        this.title = builder.title;
        this.description = builder.description;
        this.issueStatus = builder.issueStatus;
        this.approverIds = builder.approverIds;
        this.comments = builder.comments;
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

    public List<Comment> getComments()
    {
        return comments;
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

        private List<Comment> comments;

        private Builder()
        {
            this.issueStatus = IssueStatus.OPEN;
            this.approverIds = PrincipalKeys.empty();
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

        public Builder setComments( final List<Comment> comments )
        {
            this.comments = comments;
            return this;
        }

        public Builder setPublishRequest( final PublishRequest publishRequest )
        {
            this.publishRequest = publishRequest;
            return this;
        }

        public CreateIssueParams build()
        {
            return new CreateIssueParams( this );
        }
    }
}
