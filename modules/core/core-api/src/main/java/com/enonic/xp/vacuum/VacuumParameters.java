package com.enonic.xp.vacuum;

import java.time.Duration;
import java.util.Collection;
import java.util.Set;

import com.google.common.collect.ImmutableSet;

public final class VacuumParameters
{
    private final VacuumListener vacuumListener;

    private final Duration ageThreshold;

    private final ImmutableSet<String> taskNames;

    private VacuumParameters( final Builder builder )
    {
        this.vacuumListener = builder.vacuumListener;
        this.ageThreshold = builder.ageThreshold == null ? null : builder.ageThreshold;
        this.taskNames = builder.taskNames == null ? ImmutableSet.of() : ImmutableSet.copyOf( builder.taskNames );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public VacuumListener getVacuumListener()
    {
        return vacuumListener;
    }

    public Duration getAgeThreshold()
    {
        return ageThreshold;
    }

    public Set<String> getTaskNames()
    {
        return taskNames;
    }

    public static final class Builder
    {
        private VacuumListener vacuumListener;

        private Duration ageThreshold;

        private Collection<String> taskNames;

        private Builder()
        {
        }

        public Builder vacuumListener( final VacuumListener vacuumListener )
        {
            this.vacuumListener = vacuumListener;
            return this;
        }

        public Builder ageThreshold( final Duration ageThreshold )
        {
            this.ageThreshold = ageThreshold;
            return this;
        }

        public Builder taskNames( final Collection<String> taskNames )
        {
            this.taskNames = taskNames;
            return this;
        }

        public VacuumParameters build()
        {
            return new VacuumParameters( this );
        }
    }
}
