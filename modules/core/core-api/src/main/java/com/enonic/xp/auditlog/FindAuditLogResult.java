package com.enonic.xp.auditlog;

import com.google.common.base.Preconditions;

public class FindAuditLogResult
{
    private long total;

    private AuditLogs hits;

    private FindAuditLogResult( final Builder builder )
    {
        Preconditions.checkNotNull( builder.hits, "FindAuditLogResult hits cannot be null" );
        total = builder.hits.getSize();
        hits = builder.hits;
    }

    public long getTotal()
    {
        return total;
    }

    public AuditLogs getHits()
    {
        return hits;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private AuditLogs hits;

        private Builder()
        {
        }

        public Builder hits( final AuditLogs val )
        {
            hits = val;
            return this;
        }

        public FindAuditLogResult build()
        {
            return new FindAuditLogResult( this );
        }
    }
}
