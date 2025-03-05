package com.enonic.xp.impl.task;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.util.tracker.BundleTracker;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.replicatedmap.ReplicatedMap;

import com.enonic.xp.app.ApplicationBundleUtils;

class MemberAttributesApplier
    extends BundleTracker<Boolean>
{
    static final String TASKS_ENABLED_ATTRIBUTE_KEY = "tasks-enabled";

    static final String SYSTEM_TASKS_ENABLED_ATTRIBUTE_KEY = "system-tasks-enabled";

    static final String TASKS_ENABLED_ATTRIBUTE_PREFIX = TASKS_ENABLED_ATTRIBUTE_KEY + "-";

    public static final String MAP_NAME = "com.enonic.xp.impl.task";

    private final ReplicatedMap<UUID, Map<String, String>> attributesReplicatedMap;

    private final ConcurrentMap<String, String> attributes = new ConcurrentHashMap<>();

    private final UUID uuid;

    MemberAttributesApplier( final BundleContext context, final HazelcastInstance hazelcastInstance )
    {

        super( context, Bundle.ACTIVE, null );
        uuid = hazelcastInstance.getLocalEndpoint().getUuid();
        this.attributesReplicatedMap = hazelcastInstance.getReplicatedMap( MAP_NAME );
    }

    public ReplicatedMap<UUID, Map<String, String>> getAttributesReplicatedMap()
    {
        return attributesReplicatedMap;
    }

    public void activate( final TaskConfig config )
    {
        attributes.put( TASKS_ENABLED_ATTRIBUTE_KEY, String.valueOf( config.distributable_acceptInbound() ) );
        attributes.put( SYSTEM_TASKS_ENABLED_ATTRIBUTE_KEY, String.valueOf( config.distributable_acceptSystem() ) );
        attributesReplicatedMap.put( uuid, attributes );
        super.open();
    }

    public void deactivate()
    {
        super.close();
        attributes.remove( TASKS_ENABLED_ATTRIBUTE_KEY );
        attributes.remove( SYSTEM_TASKS_ENABLED_ATTRIBUTE_KEY );
        attributesReplicatedMap.put( uuid, attributes );
    }

    public void modify( final TaskConfig config )
    {
        attributes.put( TASKS_ENABLED_ATTRIBUTE_KEY, String.valueOf( config.distributable_acceptInbound() ) );
        attributes.put( SYSTEM_TASKS_ENABLED_ATTRIBUTE_KEY, String.valueOf( config.distributable_acceptSystem() ) );
        attributesReplicatedMap.put( uuid, attributes );
    }

    @Override
    public Boolean addingBundle( final Bundle bundle, final BundleEvent event )
    {
        if ( ApplicationBundleUtils.isApplication( bundle ) )
        {
            attributes.put( TASKS_ENABLED_ATTRIBUTE_PREFIX + bundle.getSymbolicName(), String.valueOf( true ) );
            attributesReplicatedMap.put( uuid, attributes );
            return true;
        }

        return null;
    }

    @Override
    public void removedBundle( final Bundle bundle, final BundleEvent event, final Boolean object )
    {
        attributes.remove( TASKS_ENABLED_ATTRIBUTE_PREFIX + bundle.getSymbolicName() );
        attributesReplicatedMap.put( uuid, attributes );
    }
}
