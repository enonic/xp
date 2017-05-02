package com.enonic.xp.admin.impl.json.issue;

public class IssueStatsJson
{
    private final long assignedToMe;

    private final long createdByMe;

    private final long open;

    private final long closed;

    private IssueStatsJson( final Builder builder )
    {
        this.assignedToMe = builder.assignedToMe;
        this.createdByMe = builder.createdByMe;
        this.open = builder.open;
        this.closed = builder.closed;
    }

    public long getAssignedToMe()
    {
        return assignedToMe;
    }

    public long getCreatedByMe()
    {
        return createdByMe;
    }

    public long getOpen()
    {
        return open;
    }

    public long getClosed()
    {
        return closed;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        private long assignedToMe = 0;

        private long createdByMe = 0;

        private long open = 0;

        private long closed = 0;

        private Builder()
        {
        }

        public Builder assignedToMe( final long value )
        {
            this.assignedToMe = value;
            return this;
        }

        public Builder createdByMe( final long value )
        {
            this.createdByMe = value;
            return this;
        }

        public Builder open( final long value )
        {
            this.open = value;
            return this;
        }

        public Builder closed( final long value )
        {
            this.closed = value;
            return this;
        }

        public IssueStatsJson build()
        {
            return new IssueStatsJson( this );
        }
    }
}
