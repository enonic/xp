package com.enonic.xp.core.impl.hazelcast;

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

import com.enonic.xp.core.internal.ApplicationBundleUtils;

class ClusterAttributesApplier
    extends BundleTracker<Boolean>
{
    static final String MAP_NAME = "com.enonic.xp.cluster";

    static final String APPLICATION_ATTRIBUTE_PREFIX = "application-";

    private final ReplicatedMap<UUID, Map<String, String>> replicatedMap;

    private final ConcurrentMap<String, String> attributes = new ConcurrentHashMap<>();

    private final UUID uuid;

    ClusterAttributesApplier( final BundleContext context, final HazelcastInstance hazelcastInstance )
    {
        super( context, Bundle.ACTIVE, null );
        this.uuid = hazelcastInstance.getCluster().getLocalMember().getUuid();
        this.replicatedMap = hazelcastInstance.getReplicatedMap( MAP_NAME );
    }

    @Override
    public Boolean addingBundle( final Bundle bundle, final BundleEvent event )
    {
        if ( ApplicationBundleUtils.isApplication( bundle ) )
        {
            attributes.put( APPLICATION_ATTRIBUTE_PREFIX + ApplicationBundleUtils.getApplicationName( bundle ), String.valueOf( true ) );
            replicatedMap.put( uuid, Map.copyOf( attributes ) );
            return true;
        }

        return null;
    }

    @Override
    public void removedBundle( final Bundle bundle, final BundleEvent event, final Boolean object )
    {
        attributes.remove( APPLICATION_ATTRIBUTE_PREFIX + ApplicationBundleUtils.getApplicationName( bundle ) );
        replicatedMap.put( uuid, Map.copyOf( attributes ) );
    }
}
