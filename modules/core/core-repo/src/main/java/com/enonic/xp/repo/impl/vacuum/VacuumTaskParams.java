package com.enonic.xp.repo.impl.vacuum;

import com.enonic.xp.vacuum.VacuumListener;
import com.enonic.xp.vacuum.VacuumTaskConfig;

public final class VacuumTaskParams
{
    private static final int DEFAULT_AGE_THRESHOLD = 60 * 60 * 1000; //1 hour

    private final long ageThreshold;

    private final VacuumListener listener;

    private final VacuumTaskConfig config;

    private VacuumTaskParams( final Builder builder )
    {
        ageThreshold = builder.ageThreshold;
        listener = builder.listener;
        config = builder.config;
    }

    public long getAgeThreshold()
    {
        return ageThreshold;
    }

    public VacuumListener getListener()
    {
        return listener;
    }

    public boolean hasListener() {
        return listener != null;
    }

    public VacuumTaskConfig getConfig()
    {
        return config;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private long ageThreshold = DEFAULT_AGE_THRESHOLD;

        private VacuumListener listener;

        private VacuumTaskConfig config;

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


        public Builder config( final VacuumTaskConfig config )
        {
            this.config = config;
            return this;
        }

        public VacuumTaskParams build()
        {
            return new VacuumTaskParams( this );
        }
    }
}
