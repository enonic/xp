package com.enonic.xp.issue;

import java.time.Instant;

import com.enonic.xp.core.internal.Millis;

public final class PublishRequestIssueSchedule
{
    private final Instant from;

    private final Instant to;

    private PublishRequestIssueSchedule( Builder builder )
    {
        from = Millis.from( builder.from );
        to = Millis.from( builder.to );
    }

    public Instant getFrom()
    {
        return from;
    }

    public Instant getTo()
    {
        return to;
    }

    public static PublishRequestIssueSchedule.Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private Instant from;

        private Instant to;

        private Builder()
        {

        }

        public Builder from( final Instant from )
        {
            this.from = from;
            return this;
        }

        public Builder to( final Instant to )
        {
            this.to = to;
            return this;
        }

        public PublishRequestIssueSchedule build()
        {
            return new PublishRequestIssueSchedule( this );
        }
    }
}
