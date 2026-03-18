package com.enonic.xp.repo.impl.elasticsearch;

import java.util.Comparator;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.hazelcast.cluster.Member;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.replicatedmap.ReplicatedMap;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.cluster.ClusterService;
import com.enonic.xp.core.internal.Local;

@Component
@Local(false)
public class HazelcastClusterServiceImpl
    implements ClusterService
{
    private static final String TASK_MAP_NAME = "com.enonic.xp.impl.task";

    private static final String TASKS_ENABLED_ATTRIBUTE_KEY = "tasks-enabled";

    private static final String TASKS_ENABLED_ATTRIBUTE_PREFIX = TASKS_ENABLED_ATTRIBUTE_KEY + "-";

    private final HazelcastInstance hazelcastInstance;

    @Activate
    public HazelcastClusterServiceImpl( @Reference final HazelcastInstance hazelcastInstance )
    {
        this.hazelcastInstance = hazelcastInstance;
    }

    @Override
    public boolean isLeader()
    {
        final UUID localMemberUuid = hazelcastInstance.getCluster().getLocalMember().getUuid();

        final Optional<UUID> leaderUuid = hazelcastInstance.getCluster()
            .getMembers()
            .stream()
            .map( Member::getUuid )
            .min( Comparator.naturalOrder() );

        return leaderUuid.map( uuid -> uuid.equals( localMemberUuid ) ).orElse( true );
    }

    @Override
    public boolean isLeader( final ApplicationKey applicationKey )
    {
        if ( applicationKey == null )
        {
            return isLeader();
        }

        final UUID localMemberUuid = hazelcastInstance.getCluster().getLocalMember().getUuid();

        final ReplicatedMap<UUID, Map<String, String>> taskAttributes = hazelcastInstance.getReplicatedMap( TASK_MAP_NAME );

        final String appAttributeKey = TASKS_ENABLED_ATTRIBUTE_PREFIX + applicationKey;

        final Stream<UUID> eligibleMembers = hazelcastInstance.getCluster()
            .getMembers()
            .stream()
            .map( Member::getUuid )
            .filter( uuid -> {
                final Map<String, String> attributes = taskAttributes.get( uuid );
                return attributes != null && Boolean.TRUE.toString().equals( attributes.get( TASKS_ENABLED_ATTRIBUTE_KEY ) ) &&
                    Boolean.TRUE.toString().equals( attributes.get( appAttributeKey ) );
            } );

        final Optional<UUID> leaderUuid = eligibleMembers.min( Comparator.naturalOrder() );

        return leaderUuid.map( uuid -> uuid.equals( localMemberUuid ) ).orElse( false );
    }
}
