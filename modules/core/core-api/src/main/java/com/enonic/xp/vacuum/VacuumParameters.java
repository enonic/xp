package com.enonic.xp.vacuum;

import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableMap;

public final class VacuumParameters
{

    public static final int DEFAULT_AGE_THRESHOLD = 60 * 60 * 1000;

    private final VacuumListener vacuumProgressListener;

    private final VacuumTaskListener vacuumTaskListener;

    private final long ageThreshold;

    private final Map<String, Map<String, Object>> taskConfigs;

    private VacuumParameters( final Builder builder )
    {
        this.vacuumProgressListener = builder.vacuumProgressListener;
        this.vacuumTaskListener = builder.vacuumTaskListener;
        this.ageThreshold = builder.ageThreshold == null ? DEFAULT_AGE_THRESHOLD : builder.ageThreshold.longValue();
        this.taskConfigs = builder.taskConfigs == null ? null : ImmutableMap.copyOf( builder.taskConfigs );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public VacuumListener getVacuumProgressListener()
    {
        return vacuumProgressListener;
    }

    public VacuumTaskListener getVacuumTaskListener()
    {
        return vacuumTaskListener;
    }

    public long getAgeThreshold()
    {
        return ageThreshold;
    }

    public Map<String, Map<String, Object>> getTaskConfigs()
    {
        return taskConfigs;
    }

    public Set<String> getTaskNames()
    {
        return taskConfigs == null ? null : taskConfigs.keySet();
    }

    public static final class Builder
    {
        private VacuumListener vacuumProgressListener;

        private VacuumTaskListener vacuumTaskListener;

        private Long ageThreshold;

        private Map<String, Map<String, Object>> taskConfigs;

        private Builder()
        {
        }

        public Builder vacuumProgressListener( final VacuumListener vacuumProgressListener )
        {
            this.vacuumProgressListener = vacuumProgressListener;
            return this;
        }

        public Builder vacuumTaskListener( final VacuumTaskListener vacuumTaskListener )
        {
            this.vacuumTaskListener = vacuumTaskListener;
            return this;
        }

        public Builder ageThreshold( final Long ageThreshold )
        {
            this.ageThreshold = ageThreshold;
            return this;
        }

        public Builder taskConfigs( final Map<String, Map<String, Object>> taskConfigs )
        {
            this.taskConfigs = taskConfigs;
            return this;
        }

        public VacuumParameters build()
        {
            return new VacuumParameters( this );
        }
    }
}
