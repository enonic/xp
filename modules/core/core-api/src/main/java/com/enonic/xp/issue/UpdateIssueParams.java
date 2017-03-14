package com.enonic.xp.issue;

import java.time.Instant;
import java.util.List;

import org.codehaus.jparsec.util.Lists;

import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalKeys;

public class UpdateIssueParams
{
    private final IssueId id;

    private final String title;

    private final String description;

    private final Instant modifiedTime;

    private final PrincipalKey modifier;

    private final IssueStatus issueStatus;

    private final PrincipalKeys approverIds;

    private final ContentIds itemIds;

    private UpdateIssueParams( Builder builder )
    {
        this.id = builder.id;
        this.title = builder.title;
        this.description = builder.description;
        this.modifiedTime = builder.modifiedTime;
        this.modifier = builder.modifier;
        this.issueStatus = builder.issueStatus;
        this.approverIds = PrincipalKeys.from( builder.approverIds );
        this.itemIds = ContentIds.from( builder.itemIds );
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

    public Instant getModifiedTime()
    {
        return modifiedTime;
    }

    public PrincipalKey getModifier()
    {
        return modifier;
    }

    public IssueStatus getStatus()
    {
        return issueStatus;
    }

    public PrincipalKeys getApproverIds()
    {
        return approverIds;
    }

    public ContentIds getItemIds()
    {
        return itemIds;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        private IssueId id;

        private String title;

        private String description;

        private Instant modifiedTime;

        private PrincipalKey modifier;

        private IssueStatus issueStatus;

        private List<PrincipalKey> approverIds;

        private List<ContentId> itemIds;

        public Builder()
        {
            final Instant now = Instant.now();
            this.modifiedTime = now;
            this.issueStatus = IssueStatus.Open;
            this.approverIds = Lists.arrayList();
            this.itemIds = Lists.arrayList();
        }

        public Builder id( final IssueId id )
        {
            this.id = id;
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

        public Builder status( final IssueStatus status )
        {
            this.issueStatus = status;
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

        public Builder addItemId( final ContentId itemId )
        {
            this.itemIds.add( itemId );
            return this;
        }

        public UpdateIssueParams build()
        {
            return new UpdateIssueParams( this );
        }
    }
}
