package com.enonic.xp.audit;

public final class CleanUpAuditLogResult
{
    private final long deleted;

    private CleanUpAuditLogResult( final Builder builder )
    {
        deleted = builder.deleted;
    }

    public long getDeleted()
    {
        return deleted;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private long deleted = 0;

        private Builder()
        {
        }

        public Builder deleted( final long value )
        {
            deleted += value;
            return this;
        }

        public CleanUpAuditLogResult build()
        {
            return new CleanUpAuditLogResult( this );
        }
    }
}
