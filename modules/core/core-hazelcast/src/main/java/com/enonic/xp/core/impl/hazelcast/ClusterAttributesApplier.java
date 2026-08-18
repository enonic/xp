package com.enonic.xp.core.impl.hazelcast;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.replicatedmap.ReplicatedMap;

import com.enonic.xp.app.Application;
import com.enonic.xp.core.internal.ApplicationBundleUtils;

class ClusterAttributesApplier
    extends ServiceTracker<Application, String>
{
    static final String MAP_NAME = "com.enonic.xp.cluster";

    static final String APPLICATION_ATTRIBUTE_PREFIX = "application-";

    private final ReplicatedMap<UUID, Map<String, String>> replicatedMap;

    private final ConcurrentMap<String, String> attributes = new ConcurrentHashMap<>();

    private final UUID uuid;

    ClusterAttributesApplier( final BundleContext context, final HazelcastInstance hazelcastInstance )
    {
        // applications are tracked by their service registration rather than by bundle state: the
        // service appears only once an application is started and configured, so a member advertises
        // applications it can actually run
        super( context, Application.class, null );
        this.uuid = hazelcastInstance.getCluster().getLocalMember().getUuid();
        this.replicatedMap = hazelcastInstance.getReplicatedMap( MAP_NAME );
    }

    @Override
    public String addingService( final ServiceReference<Application> reference )
    {
        final String attribute = APPLICATION_ATTRIBUTE_PREFIX + ApplicationBundleUtils.getApplicationName( reference.getBundle() );
        attributes.put( attribute, String.valueOf( true ) );
        replicatedMap.put( uuid, Map.copyOf( attributes ) );
        return attribute;
    }

    @Override
    public void removedService( final ServiceReference<Application> reference, final String attribute )
    {
        attributes.remove( attribute );
        replicatedMap.put( uuid, Map.copyOf( attributes ) );
    }
}
