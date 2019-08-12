package com.enonic.xp.auditlog;

import java.util.Objects;

public class FindAuditLogParams
{
    public static final int DEFAULT_FETCH_SIZE = 10;

    private final Integer start;

    private final Integer count;

    private final AuditLogIds ids;

    private FindAuditLogParams( final Builder builder )
    {
        start = Objects.requireNonNullElse( builder.start, 0 );
        count = Objects.requireNonNullElse( builder.count, 10 );
        ids = builder.ids;
    }

    public static Builder newBuilder()
    {
        return new Builder();
    }

    public AuditLogIds getIds()
    {
        return ids;
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

        public FindAuditLogParams build()
        {
            return new FindAuditLogParams( this );
        }

        public Builder start( final int val )
        {
            start = val;
            return this;
        }

        public Builder count( final int val )
        {
            count = val;
            return this;
        }
    }
}
