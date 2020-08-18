package com.enonic.xp.elasticsearch.impl;

import java.util.List;

import org.elasticsearch.Version;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthRequest;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.action.support.PlainActionFuture;
import org.elasticsearch.cluster.health.ClusterHealthStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.enonic.xp.cluster.ClusterHealth;
import com.enonic.xp.cluster.ClusterNode;
import com.enonic.xp.cluster.ClusterNodes;
import com.enonic.xp.elasticsearch.client.impl.EsClient;
import com.enonic.xp.elasticsearch.client.impl.nodes.GetNodesResponse;
import com.enonic.xp.elasticsearch.client.impl.nodes.Node;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ElasticsearchClusterTest
{
    ElasticsearchCluster activator;

    @Mock
    EsClient client;

    @BeforeEach
    public void setup()
    {
        this.activator = new ElasticsearchCluster( client );
    }

    private void setClusterHealth( final ClusterHealthStatus status )
    {
        final ClusterHealthResponse response = mock( ClusterHealthResponse.class );
        when( response.getStatus() ).thenReturn( status );

        final PlainActionFuture<ClusterHealthResponse> action = new PlainActionFuture<>();
        action.onResponse( response );

        when( this.client.clusterHealth( Mockito.any( ClusterHealthRequest.class ) ) ).thenReturn( response );
    }

    @Test
    void getNodes()
    {
        final GetNodesResponse response = mock( GetNodesResponse.class );

        final Node node1 = new Node();
        node1.setId( "testId1" );
        node1.setName( "testName1" );
        node1.setAddress( "test1" );
        node1.setRoles( List.of() );
        node1.setVersion( Version.CURRENT.toString() );

        final Node node2 = new Node();
        node2.setId( "testId2" );
        node2.setName( "testName2" );
        node2.setAddress( "test2" );
        node2.setRoles( List.of() );
        node2.setVersion( Version.CURRENT.toString() );

        final List<Node> nodes = List.of( node1, node2 );

        when( this.client.nodes() ).thenReturn( response );
        when( response.getNodes() ).thenReturn( nodes );

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
