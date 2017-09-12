package com.enonic.xp.repo.impl.vacuum;

public class VacuumTaskParams
{
    private final long ageThreshold;

    private VacuumTaskParams( final Builder builder )
    {
        ageThreshold = builder.ageThreshold;
    }

    public long getAgeThreshold()
    {
        return ageThreshold;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private long ageThreshold = 1000 * 60 * 60; // 1 hour

        private Builder()
        {
        }

        public Builder ageThreshold( final long val )
        {
            ageThreshold = val;
            return this;
        }

        public VacuumTaskParams build()
        {
            return new VacuumTaskParams( this );
        }
    }
}
