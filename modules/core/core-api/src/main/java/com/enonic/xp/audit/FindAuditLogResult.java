package com.enonic.xp.audit;

import static java.util.Objects.requireNonNull;

public final class FindAuditLogResult
{
    private final long total;

    private final AuditLogs hits;

    private FindAuditLogResult( final Builder builder )
    {
        hits = requireNonNull( builder.hits, "FindAuditLogResult hits cannot be null" );
        total = requireNonNull( builder.total, "FindAuditLogResult total cannot be null" );
    }

    public AuditLogs getHits()
    {
        return hits;
    }

    public long getTotal()
    {
        return total;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private AuditLogs hits;

        private Long total;

        private Builder()
        {
        }

        public Builder hits( final AuditLogs val )
        {
            hits = val;
            return this;
        }

        public Builder total( final Long val )
        {
            total = val;
            return this;
        }

        public FindAuditLogResult build()
        {
            return new FindAuditLogResult( this );
        }
    }
}
