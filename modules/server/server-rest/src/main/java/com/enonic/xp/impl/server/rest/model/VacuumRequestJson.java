package com.enonic.xp.impl.server.rest.model;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableMap;

import com.enonic.xp.vacuum.VacuumTaskConfig;

public class VacuumRequestJson
{
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
        return config == null ? null : (Long) config.get( "ageThreshold" );
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
