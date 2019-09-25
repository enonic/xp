package com.enonic.xp.impl.server.rest.model;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableMap;

import com.enonic.xp.vacuum.VacuumTaskConfig;

public class VacuumRequestJson
{

    public static final String AGE_THRESHOLD_KEY = "ageThreshold";

    private Map<String, Object> config;

    private Map<String, Map<String, Object>> taskConfigs;

    @JsonCreator
    public VacuumRequestJson( @JsonProperty("config") final Map<String, Object> config,
                              @JsonProperty("tasks") final Map<String, Map<String, Object>> taskConfigs )
    {
        this.config = config;
        this.taskConfigs = taskConfigs;
    }

    public Map<String, Object> getConfig()
    {
        return config;
    }

    public Long getAgeThreshold()
    {
        if ( config != null && config.containsKey( AGE_THRESHOLD_KEY ) )
        {
            final Object ageThreshold = config.get( AGE_THRESHOLD_KEY );
            if (ageThreshold instanceof Number) {
                return ((Number) ageThreshold).longValue();
            } else if (ageThreshold instanceof String) {
                return Long.parseLong( (String) ageThreshold );
            }
        }
        return null;
    }

    public Map<String, VacuumTaskConfig> getTaskConfigMap()
    {
        if ( taskConfigs == null )
        {
            return null;
        }

        final ImmutableMap.Builder<String, VacuumTaskConfig> taskConfigMap = ImmutableMap.builder();
        this.taskConfigs.entrySet().
            forEach(
                taskConfigEntry -> taskConfigMap.put( taskConfigEntry.getKey(), VacuumTaskConfig.from( taskConfigEntry.getValue() ) ) );
        return taskConfigMap.build();
    }
}
