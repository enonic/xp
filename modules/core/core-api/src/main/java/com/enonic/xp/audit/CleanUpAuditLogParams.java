package com.enonic.xp.audit;

public final class CleanUpAuditLogParams
{
    private final CleanUpAuditLogListener listener;

    private final String ageThreshold;

    private CleanUpAuditLogParams( final Builder builder )
    {
        listener = builder.listener;
        ageThreshold = builder.ageThreshold;
    }

    public CleanUpAuditLogListener getListener()
    {
        return listener;
    }

    public String getAgeThreshold()
    {
        return ageThreshold;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private CleanUpAuditLogListener listener;

        private String ageThreshold;

        private Builder()
        {
        }

        public Builder listener( final CleanUpAuditLogListener value )
        {
            listener = value;
            return this;
        }

        public Builder ageThreshold( final String value )
        {
            ageThreshold = value;
            return this;
        }

        public CleanUpAuditLogParams build()
        {
            return new CleanUpAuditLogParams( this );
        }
    }
}
