package com.enonic.xp.issue;

public final class PublishRequestIssue
    extends Issue
{
    private final PublishRequestIssueSchedule schedule;

    private PublishRequestIssue( final Builder builder )
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

    public static class Builder
        extends Issue.Builder<Builder>
    {
        private PublishRequestIssueSchedule schedule;

        private Builder()
        {
            super();
            this.issueType = IssueType.PUBLISH_REQUEST;
        }

        public Builder schedule( final PublishRequestIssueSchedule schedule )
        {
            this.schedule = schedule;
            return this;
        }

        @Override
        public PublishRequestIssue build()
        {
            return new PublishRequestIssue( this );
        }
    }
}
