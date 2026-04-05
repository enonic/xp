package com.enonic.xp.impl.task;

import java.util.Map;
import java.util.UUID;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.replicatedmap.ReplicatedMap;

class TaskAttributesApplier
{
    static final String MAP_NAME = "com.enonic.xp.task";

    static final String TASKS_ENABLED_ATTRIBUTE_KEY = "tasks-enabled";

    static final String SYSTEM_TASKS_ENABLED_ATTRIBUTE_KEY = "system-tasks-enabled";

    private final ReplicatedMap<UUID, Map<String, String>> replicatedMap;

    private final UUID uuid;

    TaskAttributesApplier( final HazelcastInstance hazelcastInstance )
    {
        this.uuid = hazelcastInstance.getCluster().getLocalMember().getUuid();
        this.replicatedMap = hazelcastInstance.getReplicatedMap( MAP_NAME );
    }

    void activate( final TaskConfig config )
    {
        replicatedMap.put( uuid, Map.of( TASKS_ENABLED_ATTRIBUTE_KEY, String.valueOf( config.distributable_acceptInbound() ),
                                         SYSTEM_TASKS_ENABLED_ATTRIBUTE_KEY, String.valueOf( config.distributable_acceptSystem() ) ) );
    }

    void modify( final TaskConfig config )
    {
        replicatedMap.put( uuid, Map.of( TASKS_ENABLED_ATTRIBUTE_KEY, String.valueOf( config.distributable_acceptInbound() ),
                                         SYSTEM_TASKS_ENABLED_ATTRIBUTE_KEY, String.valueOf( config.distributable_acceptSystem() ) ) );
    }

    void deactivate()
    {
        replicatedMap.remove( uuid );
    }
}
