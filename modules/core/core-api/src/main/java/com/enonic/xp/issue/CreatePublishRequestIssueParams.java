package com.enonic.xp.issue;

public final class CreatePublishRequestIssueParams
    extends CreateIssueParams
{
    private final PublishRequestIssueSchedule schedule;

    private CreatePublishRequestIssueParams( final Builder builder )
    {
        super( builder );
        this.schedule = builder.schedule;
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
        extends CreateIssueParams.Builder<Builder>
    {
        private PublishRequestIssueSchedule schedule;

        public Builder()
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
        public CreatePublishRequestIssueParams build()
        {
            return new CreatePublishRequestIssueParams( this );
        }
    }
}
