package com.enonic.xp.admin.impl.json.issue;

public class IssueStatsJson
{
    private final long openAssignedToMe;

    private final long openCreatedByMe;

    private final long open;

    private final long closed;

    private final long closedAssignedToMe;

    private final long closedCreatedByMe;

    private IssueStatsJson( final Builder builder )
    {
        this.openAssignedToMe = builder.openAssignedToMe;
        this.openCreatedByMe = builder.openCreatedByMe;
        this.open = builder.open;
        this.closed = builder.closed;
        this.closedAssignedToMe = builder.closedAssignedToMe;
        this.closedCreatedByMe = builder.closedCreatedByMe;
    }

    public long getOpenAssignedToMe()
    {
        return openAssignedToMe;
    }

    public long getOpenCreatedByMe()
    {
        return openCreatedByMe;
    }

    public long getOpen()
    {
        return open;
    }

    public long getClosed()
    {
        return closed;
    }

    public long getClosedAssignedToMe()
    {
        return closedAssignedToMe;
    }

    public long getClosedCreatedByMe()
    {
        return closedCreatedByMe;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        private long openAssignedToMe = 0;

        private long openCreatedByMe = 0;

        private long open = 0;

        private long closed = 0;

        private long closedAssignedToMe = 0;

        private long closedCreatedByMe = 0;

        private Builder()
        {
        }

        public Builder openAssignedToMe( final long value )
        {
            this.openAssignedToMe = value;
            return this;
        }

        public Builder openCreatedByMe( final long value )
        {
            this.openCreatedByMe = value;
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

        public Builder closedAssignedToMe( final long value )
        {
            this.closedAssignedToMe = value;
            return this;
        }

        public Builder closedCreatedByMe( final long value )
        {
            this.closedCreatedByMe = value;
            return this;
        }

        public IssueStatsJson build()
        {
            return new IssueStatsJson( this );
        }
    }
}
