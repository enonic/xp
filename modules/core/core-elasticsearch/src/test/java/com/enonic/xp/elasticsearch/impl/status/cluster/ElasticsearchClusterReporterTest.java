package com.enonic.xp.elasticsearch.impl.status.cluster;

import java.net.InetAddress;
import java.util.List;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.Version;
import org.elasticsearch.action.ActionFuture;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.action.admin.cluster.state.ClusterStateResponse;
import org.elasticsearch.client.ClusterAdminClient;
import org.elasticsearch.cluster.ClusterInfoService;
import org.elasticsearch.cluster.ClusterName;
import org.elasticsearch.cluster.ClusterState;
import org.elasticsearch.cluster.health.ClusterHealthStatus;
import org.elasticsearch.cluster.node.DiscoveryNode;
import org.elasticsearch.cluster.node.DiscoveryNodes;
import org.elasticsearch.common.transport.TransportAddress;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import com.enonic.xp.status.JsonStatusReporterTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.mockito.Mockito.when;

@Disabled
public class ElasticsearchClusterReporterTest
    extends JsonStatusReporterTest
{
    private final ElasticsearchClusterReporter clusterReporter = new ElasticsearchClusterReporter();

    private ClusterState clusterState;

    private ClusterInfoService clusterService;

    private ActionFuture<ClusterStateResponse> clusterStateInfo;

    private ActionFuture<ClusterHealthResponse> clusterHealthInfo;

    @SuppressWarnings("unchecked")
    @BeforeEach
    public void setup()
        throws Exception
    {
        final ClusterAdminClient clusterAdminClient = Mockito.mock( ClusterAdminClient.class );
        this.clusterStateInfo = Mockito.mock( ActionFuture.class );
        this.clusterHealthInfo = Mockito.mock( ActionFuture.class );

        this.clusterService = Mockito.mock( ClusterInfoService.class );

        when( clusterAdminClient.state( Mockito.any() ) ).thenReturn( clusterStateInfo );
        when( clusterAdminClient.health( Mockito.any() ) ).thenReturn( clusterHealthInfo );

        this.clusterState = Mockito.mock( ClusterState.class );

        final ClusterStateResponse clusterStateResponse = Mockito.mock( ClusterStateResponse.class );
        final ClusterHealthResponse clusterHealthResponse = Mockito.mock( ClusterHealthResponse.class );

        when( clusterStateInfo.actionGet() ).thenReturn( clusterStateResponse );
        when( clusterHealthInfo.actionGet() ).thenReturn( clusterHealthResponse );

        final ClusterName clusterName = new ClusterName( "clusterName" );
        when( clusterState.getClusterName() ).thenReturn( clusterName );
        when( clusterStateResponse.getClusterName() ).thenReturn( clusterName );

        when( clusterStateResponse.getState() ).thenReturn( clusterState );
        when( clusterHealthResponse.getStatus() ).thenReturn( ClusterHealthStatus.GREEN );

        final ClusterHealthProvider clusterHealthProvider = new ClusterHealthProvider();
        //clusterHealthProvider.setClusterAdminClient( clusterAdminClient );

        final ClusterStateProvider clusterStateProvider = new ClusterStateProvider();
        //clusterStateProvider.setClient( clusterAdminClient );

        clusterReporter.setClusterHealthProvider( clusterHealthProvider );
        clusterReporter.setClusterStateProvider( clusterStateProvider );
    }

    @Test
    public void assertName()
        throws Exception
    {
        assertEquals( "cluster.elasticsearch", clusterReporter.getName() );
    }

    @Test
    public void cluster_with_one_node()
        throws Exception
    {
        final DiscoveryNode node1 = new DiscoveryNode( "nodeName", new TransportAddress( InetAddress.getByName( "10.10.10.1" ), 9300 ),
                                                       Version.fromString( "1.0.0" ) );

        final DiscoveryNodes nodes = DiscoveryNodes.builder().
            add( node1 ).
            localNodeId( node1.getId() ).
            build();

        Mockito.when( clusterState.getNodes() ).thenReturn( nodes );

        assertJson( "cluster_with_one_node.json", clusterReporter.getReport().toString() );
    }

    @Test
    public void testClusterState_Exception()
        throws Exception
    {
        Mockito.when( clusterStateInfo.actionGet() ).thenThrow( new ElasticsearchException( "cluster state exception" ) );

        assertJson( "cluster_state_exception.json", clusterReporter.getReport().toString() );
    }

    @Test
    public void testClusterHealth_Exception()
        throws Exception
    {
        Mockito.when( clusterHealthInfo.actionGet() ).thenThrow( new ElasticsearchException( "cluster health info exception" ) );

        final DiscoveryNode node1 = new DiscoveryNode( "nodeName", new TransportAddress( InetAddress.getByName( "10.10.10.1" ), 9300 ),
                                                       Version.fromString( "1.0.0" ) );

        final DiscoveryNodes nodes = DiscoveryNodes.builder().
            add( node1 ).
            localNodeId( node1.getId() ).
            build();

        Mockito.when( clusterState.getNodes() ).thenReturn( nodes );

        assertJson( "cluster_health_info_exception.json", clusterReporter.getReport().toString() );
    }

    @Test
    public void cluster_with_two_nodes()
        throws Exception
    {
        final DiscoveryNode node1 = new DiscoveryNode( "nodeName", new TransportAddress( InetAddress.getByName( "10.10.10.1" ), 9300 ),
                                                       Version.fromString( "1.0.0" ) );

        final DiscoveryNode node2 = new DiscoveryNode( "nodeName", new TransportAddress( InetAddress.getByName( "10.10.10.2" ), 9300 ),
                                                       Version.fromString( "1.0.0" ) );

        final DiscoveryNodes nodes = DiscoveryNodes.builder().
            add( node1 ).
            add( node2 ).
            localNodeId( node1.getId() ).
            masterNodeId( node2.getId() ).
            build();

        Mockito.when( clusterState.getNodes() ).thenReturn( nodes );

        final JsonNode expectedReport = parseJson( readFromFile( "cluster_with_two_nodes.json" ) );
        final JsonNode report = clusterReporter.getReport();
        assertEquals( expectedReport.get( "name" ), report.get( "name" ) );
        assertEquals( expectedReport.get( "localNode" ), report.get( "localNode" ) );
        assertEquals( expectedReport.get( "state" ), report.get( "state" ) );

        final ArrayNode expectedMembers = (ArrayNode) expectedReport.get( "members" );
        final ArrayNode members = (ArrayNode) report.get( "members" );
        assertIterableEquals( List.of( members.get( 0 ), members.get( 1 ) ), expectedMembers );
    }


    private final void assertJson( final String fileName, final String actualJson )
        throws Exception
    {
        final JsonNode expectedNode = parseJson( readFromFile( fileName ) );
        final JsonNode actualNode = parseJson( actualJson );

        final String expectedStr = toJson( expectedNode );
        final String actualStr = toJson( actualNode );

        assertEquals( expectedStr, actualStr );
    }
}
