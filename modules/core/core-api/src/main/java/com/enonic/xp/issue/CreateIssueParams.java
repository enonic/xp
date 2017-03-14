package com.enonic.xp.issue;

import java.util.List;

import org.codehaus.jparsec.util.Lists;

import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.name.NamePrettyfier;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalKeys;

public class CreateIssueParams
{
    private final IssueId id;

    private final String title;

    private final IssueName name;

    private final String description;

    private final IssueStatus issueStatus;

    private final PrincipalKeys approverIds;

    private final ContentIds itemIds;

    private CreateIssueParams( Builder builder )
    {
        this.id = IssueId.create();
        this.title = builder.title;
        this.name = IssueName.from( NamePrettyfier.create( builder.title ) );
        this.description = builder.description;
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

    public IssueName getName()
    {
        return name;
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

        private String title;

        private String description;

        private IssueStatus issueStatus;

        private List<PrincipalKey> approverIds;

        private List<ContentId> itemIds;

        public Builder()
        {
            this.issueStatus = IssueStatus.Open;
            this.approverIds = Lists.arrayList();
            this.itemIds = Lists.arrayList();
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

        public CreateIssueParams build()
        {
            return new CreateIssueParams( this );
        }
    }
}
