package com.enonic.xp.issue;


import com.enonic.xp.content.ContentIds;

@Deprecated
public final class FindIssuesParams
{
    private final IssueStatus status;

    private final IssueType type;

    private final boolean assignedToMe;

    private final boolean createdByMe;

    private final ContentIds items;

    private final Integer from;

    private final Integer size;

    private FindIssuesParams( final Builder builder )
    {
        this.status = builder.status;
        this.type = builder.type;
        this.assignedToMe = builder.assignedToMe;
        this.createdByMe = builder.createdByMe;
        this.from = builder.from;
        this.size = builder.size;
        this.items = builder.items;
    }

    public IssueStatus getStatus()
    {
        return status;
    }

    public IssueType getType()
    {
        return type;
    }

    public boolean isAssignedToMe()
    {
        return assignedToMe;
    }

    public boolean isCreatedByMe()
    {
        return createdByMe;
    }

    public Integer getFrom()
    {
        return from;
    }

    public Integer getSize()
    {
        return size;
    }

    public ContentIds getItems()
    {
        return items;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        private IssueStatus status;

        private IssueType type;

        private boolean assignedToMe = false;

        private boolean createdByMe = false;

        private ContentIds items;

        private Integer from;

        private Integer size;

        private Builder()
        {
        }

        public Builder status( final IssueStatus value )
        {
            this.status = value;
            return this;
        }

        public Builder type( final IssueType value )
        {
            this.type = value;
            return this;
        }

        public Builder assignedToMe( final boolean value )
        {
            this.assignedToMe = value;
            return this;
        }

        public Builder createdByMe( final boolean value )
        {
            this.createdByMe = value;
            return this;
        }

        public Builder from( final Integer value )
        {
            this.from = value;
            return this;
        }

        public Builder size( final Integer value )
        {
            this.size = value;
            return this;
        }

        public Builder items( final ContentIds contentIds )
        {
            this.items = contentIds;
            return this;
        }

        public FindIssuesParams build()
        {
            return new FindIssuesParams( this );
        }
    }
}
