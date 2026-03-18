package com.enonic.xp.repo.impl.elasticsearch;

import java.util.Comparator;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import org.elasticsearch.action.admin.cluster.state.ClusterStateAction;
import org.elasticsearch.action.admin.cluster.state.ClusterStateRequestBuilder;
import org.elasticsearch.action.admin.cluster.state.ClusterStateResponse;
import org.elasticsearch.client.Client;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hazelcast.cluster.Member;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.replicatedmap.ReplicatedMap;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.cluster.ClusterService;

@Component
public class ClusterServiceImpl
    implements ClusterService
{
    private static final Logger LOG = LoggerFactory.getLogger( ClusterServiceImpl.class );

    private static final String CLUSTER_STATE_TIMEOUT = "5s";

    private static final String TASK_MAP_NAME = "com.enonic.xp.impl.task";

    private static final String TASKS_ENABLED_ATTRIBUTE_KEY = "tasks-enabled";

    private static final String TASKS_ENABLED_ATTRIBUTE_PREFIX = TASKS_ENABLED_ATTRIBUTE_KEY + "-";

    private final Client client;

    private volatile HazelcastInstance hazelcastInstance;

    @Activate
    public ClusterServiceImpl( @Reference final Client client )
    {
        this.client = client;
    }

    @Reference(cardinality = ReferenceCardinality.OPTIONAL)
    public void setHazelcastInstance( final HazelcastInstance hazelcastInstance )
    {
        this.hazelcastInstance = hazelcastInstance;
    }

    public void unsetHazelcastInstance( final HazelcastInstance hazelcastInstance )
    {
        this.hazelcastInstance = null;
    }

    @Override
    public boolean isLeader()
    {
        if ( hazelcastInstance == null )
        {
            LOG.warn( "Hazelcast instance not available, falling back to isMaster" );
            return isMaster();
        }

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

        if ( hazelcastInstance == null )
        {
            LOG.warn( "Hazelcast instance not available, falling back to isMaster" );
            return isMaster();
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

    private boolean isMaster()
    {
        final ClusterStateRequestBuilder requestBuilder =
            new ClusterStateRequestBuilder( this.client.admin().cluster(), ClusterStateAction.INSTANCE ).setBlocks( false )
                .setIndices()
                .setBlocks( false )
                .setMetaData( false )
                .setNodes( true )
                .setRoutingTable( false );

        final ClusterStateResponse clusterStateResponse =
            client.admin().cluster().state( requestBuilder.request() ).actionGet( CLUSTER_STATE_TIMEOUT );

        return clusterStateResponse.getState().nodes().localNodeMaster();
    }
}
