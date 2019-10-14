package com.enonic.xp.repo.impl.vacuum;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

import com.enonic.xp.vacuum.VacuumListener;

public final class VacuumTaskParams
{
    private final long ageThreshold;

    private final VacuumListener listener;

    private VacuumTaskParams( final Builder builder )
    {
        ageThreshold = builder.ageThreshold;
        listener = builder.listener;
    }

    public long getAgeThreshold()
    {
        return ageThreshold;
    }

    public VacuumListener getListener()
    {
        return listener;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private long ageThreshold = Duration.of( 1, ChronoUnit.HOURS ).toMillis();

        private VacuumListener listener;

        private Builder()
        {
        }

        public Builder ageThreshold( final long val )
        {
            ageThreshold = val;
            return this;
        }

        public Builder listener( final VacuumListener listener )
        {
            this.listener = listener;
            return this;
        }

        public VacuumTaskParams build()
        {
            return new VacuumTaskParams( this );
        }
    }
}
