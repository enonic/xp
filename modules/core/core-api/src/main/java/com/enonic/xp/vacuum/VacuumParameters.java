package com.enonic.xp.vacuum;

import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableMap;

public final class VacuumParameters
{
    private static final int DEFAULT_AGE_THRESHOLD = 60 * 60 * 1000; //1 hour

    private final VacuumListener vacuumListener;

    private final long ageThreshold;

    private final Map<String, VacuumTaskConfig> taskConfigMap;

    private VacuumParameters( final Builder builder )
    {
        this.vacuumListener = builder.vacuumListener;
        this.ageThreshold = builder.ageThreshold == null ? DEFAULT_AGE_THRESHOLD : builder.ageThreshold.longValue();
        this.taskConfigMap = builder.taskConfigMap == null ? null : ImmutableMap.copyOf( builder.taskConfigMap );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public VacuumListener getVacuumListener()
    {
        return vacuumListener;
    }

    public long getAgeThreshold()
    {
        return ageThreshold;
    }

    public Map<String, VacuumTaskConfig> getTaskConfigMap()
    {
        return taskConfigMap;
    }

    public VacuumTaskConfig getTaskConfig(final String taskName) {
        return taskConfigMap == null ? null : taskConfigMap.get( taskName );
    }

    public Set<String> getTaskNames()
    {
        return taskConfigMap == null ? null : taskConfigMap.keySet();
    }

    public static final class Builder
    {
        private VacuumListener vacuumListener;

        private Long ageThreshold;

        private Map<String, VacuumTaskConfig> taskConfigMap;

        private Builder()
        {
        }

        public Builder vacuumListener( final VacuumListener vacuumListener )
        {
            this.vacuumListener = vacuumListener;
            return this;
        }

        public Builder ageThreshold( final Long ageThreshold )
        {
            this.ageThreshold = ageThreshold;
            return this;
        }

        public Builder taskConfigMap( final Map<String, VacuumTaskConfig> taskConfigs )
        {
            this.taskConfigMap = taskConfigs;
            return this;
        }

        public VacuumParameters build()
        {
            return new VacuumParameters( this );
        }
    }
}
