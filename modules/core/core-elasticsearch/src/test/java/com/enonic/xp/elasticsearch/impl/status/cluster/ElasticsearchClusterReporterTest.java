package com.enonic.xp.elasticsearch.impl.status.cluster;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.Version;
import org.elasticsearch.action.ActionFuture;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.action.admin.cluster.state.ClusterStateResponse;
import org.elasticsearch.client.ClusterAdminClient;
import org.elasticsearch.cluster.ClusterName;
import org.elasticsearch.cluster.ClusterService;
import org.elasticsearch.cluster.ClusterState;
import org.elasticsearch.cluster.health.ClusterHealthStatus;
import org.elasticsearch.cluster.node.DiscoveryNode;
import org.elasticsearch.cluster.node.DiscoveryNodes;
import org.elasticsearch.common.transport.LocalTransportAddress;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.google.common.net.MediaType;

import com.enonic.xp.support.JsonTestHelper;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ElasticsearchClusterReporterTest
{
    JsonTestHelper jsonTestHelper = new JsonTestHelper( this );

    private ElasticsearchClusterReporter reporter;

    private ClusterState clusterState;

    private ClusterService clusterService;

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

        this.clusterService = Mockito.mock( ClusterService.class );

        Mockito.when( clusterAdminClient.state( Mockito.any() ) ).thenReturn( clusterStateInfo );
        Mockito.when( clusterAdminClient.health( Mockito.any() ) ).thenReturn( clusterHealthInfo );

        this.clusterState = Mockito.mock( ClusterState.class );

        final ClusterStateResponse clusterStateResponse = Mockito.mock( ClusterStateResponse.class );
        final ClusterHealthResponse clusterHealthResponse = Mockito.mock( ClusterHealthResponse.class );

        Mockito.when( clusterStateInfo.actionGet() ).thenReturn( clusterStateResponse );
        Mockito.when( clusterHealthInfo.actionGet() ).thenReturn( clusterHealthResponse );

        final ClusterName clusterName = new ClusterName( "clusterName" );
        Mockito.when( clusterState.getClusterName() ).thenReturn( clusterName );
        Mockito.when( clusterStateResponse.getClusterName() ).thenReturn( clusterName );

        Mockito.when( clusterStateResponse.getState() ).thenReturn( clusterState );
        Mockito.when( clusterHealthResponse.getStatus() ).thenReturn( ClusterHealthStatus.GREEN );

        final ClusterHealthProvider clusterHealthProvider = new ClusterHealthProvider();
        clusterHealthProvider.setClusterAdminClient( clusterAdminClient );

        final ClusterStateProvider clusterStateProvider = new ClusterStateProvider();
        clusterStateProvider.setClusterAdminClient( clusterAdminClient );
        clusterStateProvider.setClusterService( clusterService );

        reporter = new ElasticsearchClusterReporter( clusterStateProvider, clusterHealthProvider);
    }

    @Test
    public void assertName()
        throws Exception
    {
        assertEquals( "cluster.elasticsearch", reporter.getName() );
    }

    @Test
    public void cluster_with_one_node()
        throws Exception
    {
        final DiscoveryNode node1 =
            new DiscoveryNode( "nodeName", "nodeId", "hostName", "hostAddress", new LocalTransportAddress( "10.10.10.1" ), new HashMap<>(),
                               Version.fromString( "1.0.0" ) );

        final DiscoveryNodes nodes = DiscoveryNodes.builder().
            put( node1 ).
            localNodeId( node1.getId() ).
            build();

        Mockito.when( clusterState.getNodes() ).thenReturn( nodes );
        Mockito.when( this.clusterService.localNode() ).thenReturn( node1 );

        assertJson( "cluster_with_one_node.json" );
    }

    @Test
    public void testClusterState_Exception()
        throws Exception
    {
        Mockito.when( clusterStateInfo.actionGet() ).thenThrow( new ElasticsearchException( "cluster state exception" ) );

        assertJson( "cluster_state_exception.json" );
    }

    @Test
    public void testClusterHealth_Exception()
        throws Exception
    {
        Mockito.when( clusterHealthInfo.actionGet() ).thenThrow( new ElasticsearchException( "cluster health info exception" ) );

        final DiscoveryNode node1 =
            new DiscoveryNode( "nodeName", "nodeId", "hostName", "hostAddress", new LocalTransportAddress( "10.10.10.1" ), new HashMap<>(),
                               Version.fromString( "1.0.0" ) );

        final DiscoveryNodes nodes = DiscoveryNodes.builder().
            put( node1 ).
            localNodeId( node1.getId() ).
            build();

        Mockito.when( clusterState.getNodes() ).thenReturn( nodes );
        Mockito.when( this.clusterService.localNode() ).thenReturn( node1 );


        assertJson( "cluster_health_info_exception.json" );
    }

    private void assertJson( final String fileName )
        throws Exception
    {
        assertEquals( MediaType.JSON_UTF_8, reporter.getMediaType() );

        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        reporter.report( outputStream );

        jsonTestHelper.assertJsonEquals( jsonTestHelper.loadTestJson( fileName ),
                                         jsonTestHelper.bytesToJson( outputStream.toByteArray() ) );
    }
}
