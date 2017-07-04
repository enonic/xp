package com.enonic.xp.issue;

public final class FindIssuesParams
{
    private final IssueStatus status;

    private final boolean assignedToMe;

    private final boolean createdByMe;

    private final int from;

    private final int size;

    private FindIssuesParams( final Builder builder )
    {
        this.status = builder.status;
        this.assignedToMe = builder.assignedToMe;
        this.createdByMe = builder.createdByMe;
        this.from = builder.from;
        this.size = builder.size;
    }

    public IssueStatus getStatus()
    {
        return status;
    }

    public boolean isAssignedToMe()
    {
        return assignedToMe;
    }

    public boolean isCreatedByMe()
    {
        return createdByMe;
    }

    public int getFrom()
    {
        return from;
    }

    public int getSize()
    {
        return size;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        private IssueStatus status;

        private boolean assignedToMe = false;

        private boolean createdByMe = false;

        private int from = 0;

        private int size = 0;

        private Builder()
        {
        }

        public Builder status( final IssueStatus value )
        {
            this.status = value;
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
            if ( value != null )
            {
                this.from = value;
            }
            return this;
        }

        public Builder size( final Integer value )
        {
            if ( value != null )
            {
                this.size = value;
            }
            return this;
        }

        public FindIssuesParams build()
        {
            return new FindIssuesParams( this );
        }
    }
}
