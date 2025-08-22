package com.enonic.xp.audit;

import java.util.Objects;

public final class FindAuditLogResult
{
    private final long count;

    private final long total;

    private final AuditLogs hits;

    private FindAuditLogResult( final Builder builder )
    {
        hits = Objects.requireNonNull( builder.hits, "FindAuditLogResult hits cannot be null" );
        count = builder.hits.getSize();
        total = Objects.requireNonNull( builder.total, "FindAuditLogResult total cannot be null" );
    }

    public long getCount()
    {
        return count;
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

    public static FindAuditLogResult empty()
    {
        return FindAuditLogResult.create().
            total( 0L ).
            hits( AuditLogs.empty() ).
            build();
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
