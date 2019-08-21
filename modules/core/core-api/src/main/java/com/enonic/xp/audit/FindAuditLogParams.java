package com.enonic.xp.audit;

import java.time.Instant;
import java.util.Objects;

public class FindAuditLogParams
{
    public static final int DEFAULT_FETCH_SIZE = 10;

    private final Integer start;

    private final Integer count;

    private final AuditLogIds ids;

    private final Instant from;

    private final Instant to;

    private final String type;

    private final String source;

    private FindAuditLogParams( final Builder builder )
    {
        start = Objects.requireNonNullElse( builder.start, 0 );
        count = Objects.requireNonNullElse( builder.count, DEFAULT_FETCH_SIZE );
        ids = builder.ids;
        from = builder.from;
        to = builder.to;
        type = builder.type;
        source = builder.source;
    }

    public static Builder newBuilder()
    {
        return new Builder();
    }

    public AuditLogIds getIds()
    {
        return ids;
    }

    public Instant getFrom()
    {
        return from;
    }

    public Instant getTo()
    {
        return to;
    }

    public String getType()
    {
        return type;
    }

    public String getSource()
    {
        return source;
    }

    public int getStart()
    {
        return start;
    }

    public int getCount()
    {
        return count;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {

        private AuditLogIds ids;

        private Instant from;

        private Instant to;

        private String type;

        private String source;

        private Integer start;

        private Integer count;

        private Builder()
        {
        }

        public Builder ids( final AuditLogIds val )
        {
            ids = val;
            return this;
        }

        public Builder from( final Instant val )
        {
            from = val;
            return this;
        }

        public Builder to( final Instant val )
        {
            to = val;
            return this;
        }

        public Builder type( final String val )
        {
            type = val;
            return this;
        }

        public Builder source( final String val )
        {
            source = val;
            return this;
        }

        public FindAuditLogParams build()
        {
            return new FindAuditLogParams( this );
        }

        public Builder start( final Integer val )
        {
            start = val;
            return this;
        }

        public Builder count( final Integer val )
        {
            count = val;
            return this;
        }
    }
}
