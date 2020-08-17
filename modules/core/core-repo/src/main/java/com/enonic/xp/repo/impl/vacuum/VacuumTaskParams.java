package com.enonic.xp.repo.impl.vacuum;

import java.time.Duration;

import com.enonic.xp.vacuum.VacuumListener;

public final class VacuumTaskParams
{
    public static final int DEFAULT_VERSIONS_BATCH_SIZE = 10_000;

    private final long ageThreshold;

    private final VacuumListener listener;

    private final int versionsBatchSize;

    private VacuumTaskParams( final Builder builder )
    {
        ageThreshold = builder.ageThreshold;
        listener = builder.listener;
        versionsBatchSize = builder.versionsBatchSize;
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

    public int getVersionsBatchSize()
    {
        return versionsBatchSize;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private long ageThreshold = Duration.ofDays( 21 ).toMillis();

        private VacuumListener listener;

        private int versionsBatchSize = DEFAULT_VERSIONS_BATCH_SIZE;

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

        public Builder versionsBatchSize( final int versionsBatchSize )
        {
            this.versionsBatchSize = versionsBatchSize;
            return this;
        }

        public VacuumTaskParams build()
        {
            return new VacuumTaskParams( this );
        }
    }
}
