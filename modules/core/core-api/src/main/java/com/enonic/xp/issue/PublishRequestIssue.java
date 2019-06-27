package com.enonic.xp.issue;

import com.google.common.base.Preconditions;

import com.enonic.xp.data.PropertyTree;

public class PublishRequestIssue
    extends Issue
{
    private static final PublishRequestIssueScheduleSerializer REQUEST_ISSUE_SCHEDULE_SERIALIZER =
        new PublishRequestIssueScheduleSerializer();

    protected PublishRequestIssue( final Builder builder )
    {
        super( builder );
    }

    public PublishRequestIssueSchedule getSchedule()
    {
        return REQUEST_ISSUE_SCHEDULE_SERIALIZER.fromData( getData() );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
        extends Issue.Builder<Builder>
    {

        private Builder()
        {
            super();
            this.issueType = IssueType.PUBLISH_REQUEST_ISSUE;
        }

        public Builder schedule( final PublishRequestIssueSchedule schedule )
        {
            if ( this.data == null )
            {
                this.data = new PropertyTree();
            }
            REQUEST_ISSUE_SCHEDULE_SERIALIZER.toData( schedule, data );
            return this;
        }

        @Override
        public PublishRequestIssue build()
        {
            Preconditions.checkNotNull( this.data, "Publish request issue schedule cannot be null" );
            return new PublishRequestIssue( this );
        }
    }
}
