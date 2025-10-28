package com.enonic.xp.elasticsearch.impl;

import java.util.Map;

import org.elasticsearch.Version;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.action.admin.cluster.state.ClusterStateResponse;
import org.elasticsearch.action.support.PlainActionFuture;
import org.elasticsearch.client.ClusterAdminClient;
import org.elasticsearch.cluster.health.ClusterHealthStatus;
import org.elasticsearch.cluster.node.DiscoveryNode;
import org.elasticsearch.cluster.node.DiscoveryNodes;
import org.elasticsearch.common.transport.DummyTransportAddress;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.enonic.xp.cluster.ClusterHealth;
import com.enonic.xp.cluster.ClusterNode;
import com.enonic.xp.cluster.ClusterNodes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ElasticsearchClusterTest
{
    ElasticsearchCluster activator;

    @Mock
    ClusterAdminClient clusterAdminClient;

    @BeforeEach
    void setup()
    {
        this.activator = new ElasticsearchCluster( clusterAdminClient );
    }

    private void setClusterHealth( final ClusterHealthStatus status )
    {
        final ClusterHealthResponse response = mock( ClusterHealthResponse.class );
        when( response.getStatus() ).thenReturn( status );

        final PlainActionFuture<ClusterHealthResponse> action = new PlainActionFuture<>();
        action.onResponse( response );

        when( this.clusterAdminClient.health( Mockito.any() ) ).thenReturn( action );
    }

    @Test
    void getNodes()
    {
        final ClusterStateResponse response = mock( ClusterStateResponse.class, RETURNS_DEEP_STUBS );

        final DiscoveryNodes nodes = DiscoveryNodes.builder().
            put( new DiscoveryNode( "testName1", "test1", DummyTransportAddress.INSTANCE, Map.of(), Version.CURRENT ) ).
            put( new DiscoveryNode( "testName2", "test2", DummyTransportAddress.INSTANCE, Map.of(), Version.CURRENT ) ).
            build();

        when( response.getState().getNodes() ).thenReturn( nodes );

        final PlainActionFuture<ClusterStateResponse> action = new PlainActionFuture<>();
        action.onResponse( response );

        when( this.clusterAdminClient.state( Mockito.any() ) ).thenReturn( action );

        final ClusterNodes expected = ClusterNodes.create().
            add( ClusterNode.from( "testName1" ) ).
            add( ClusterNode.from( "testName2" ) ).
            build();
        final ClusterNodes actual = activator.getNodes();

        assertEquals( expected, actual );
    }

    @Test
    void health_red()
    {
        setClusterHealth( ClusterHealthStatus.RED );
        assertEquals( ClusterHealth.red().getStatus(), this.activator.getHealth().getStatus() );
    }

    @Test
    void health_green()
    {
        setClusterHealth( ClusterHealthStatus.YELLOW );
        assertEquals( ClusterHealth.yellow(), this.activator.getHealth() );
    }

    @Test
    void health_yellow()
    {
        setClusterHealth( ClusterHealthStatus.GREEN );
        assertEquals( ClusterHealth.green(), this.activator.getHealth() );
    }
}
