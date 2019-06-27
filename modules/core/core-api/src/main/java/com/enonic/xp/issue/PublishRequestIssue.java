package com.enonic.xp.issue;

import com.google.common.base.Preconditions;

import com.enonic.xp.data.PropertyTree;

public class PublishRequestIssue
    extends Issue
{
    private static final String DATA_NAME_FROM = "schedule.from";

    private static final String DATA_NAME_TO = "schedule.to";

    private PublishRequestIssueSchedule schedule;

    protected PublishRequestIssue( final Builder builder )
    {
        super( builder );
        schedule = builder.schedule;
    }

    public PublishRequestIssueSchedule getSchedule()
    {
        return schedule;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static Builder create( final PublishRequestIssue source )
    {
        return new Builder( source );
    }

    public static class Builder
        extends Issue.Builder<Builder>
    {
        private PublishRequestIssueSchedule schedule;

        private Builder()
        {
            super();
            this.issueType = IssueType.PUBLISH_REQUEST_ISSUE;
        }

        protected Builder( final PublishRequestIssue source )
        {
            super( source );
            this.issueType = IssueType.PUBLISH_REQUEST_ISSUE;
            schedule = source.schedule;
        }

        public Builder schedule( final PublishRequestIssueSchedule schedule )
        {
            if ( this.data == null )
            {
                this.data = new PropertyTree();
            }

            this.data.setInstant( DATA_NAME_FROM, schedule.getFrom() );
            this.data.setInstant( DATA_NAME_TO, schedule.getTo() );
            return this;
        }

        @Override
        public Builder data( final PropertyTree data )
        {
            return super.data( data );
        }

        @Override
        public PublishRequestIssue build()
        {
            if ( this.data != null )
            {
                this.schedule = PublishRequestIssueSchedule.create().
                    from( data.getInstant( DATA_NAME_FROM ) ).
                    to( data.getInstant( DATA_NAME_TO ) ).
                    build();
            }

            Preconditions.checkNotNull( this.schedule, "Publish request issue schedule cannot be null" );

            return new PublishRequestIssue( this );
        }
    }
}
