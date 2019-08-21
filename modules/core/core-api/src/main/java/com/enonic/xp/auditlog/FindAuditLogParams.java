package com.enonic.xp.auditlog;

public class FindAuditLogParams
{

    private final AuditLogIds ids;

    private FindAuditLogParams( final Builder builder )
    {
        ids = builder.ids;
    }

    public AuditLogIds getIds()
    {
        return ids;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private AuditLogIds ids;

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
    }
}
