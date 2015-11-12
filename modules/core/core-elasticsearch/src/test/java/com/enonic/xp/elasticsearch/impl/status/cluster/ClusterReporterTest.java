package com.enonic.xp.elasticsearch.impl.status.cluster;

import java.net.URL;
import java.util.HashMap;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.Version;
import org.elasticsearch.action.ActionFuture;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthStatus;
import org.elasticsearch.action.admin.cluster.node.info.NodeInfo;
import org.elasticsearch.action.admin.cluster.node.info.NodesInfoResponse;
import org.elasticsearch.action.admin.cluster.state.ClusterStateResponse;
import org.elasticsearch.client.ClusterAdminClient;
import org.elasticsearch.cluster.ClusterName;
import org.elasticsearch.cluster.ClusterState;
import org.elasticsearch.cluster.node.DiscoveryNode;
import org.elasticsearch.cluster.node.DiscoveryNodes;
import org.elasticsearch.common.transport.LocalTransportAddress;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;


public class ClusterReporterTest
{
    private final ClusterReporter clusterReporter = new ClusterReporter();

    private ClusterState clusterState;

    private ActionFuture<ClusterStateResponse> clusterStateInfo;

    private ActionFuture<NodesInfoResponse> nodesInfo;

    private ActionFuture<ClusterHealthResponse> clusterHealthInfo;

    private NodeInfo localNodeInfo;

    @SuppressWarnings("unchecked")
    @Before
    public void setup()
        throws Exception
    {
        final ClusterAdminClient clusterAdminClient = Mockito.mock( ClusterAdminClient.class );
        this.clusterStateInfo = Mockito.mock( ActionFuture.class );
        this.nodesInfo = Mockito.mock( ActionFuture.class );
        this.clusterHealthInfo = Mockito.mock( ActionFuture.class );

        Mockito.when( clusterAdminClient.state( Mockito.any() ) ).thenReturn( clusterStateInfo );
        Mockito.when( clusterAdminClient.nodesInfo( Mockito.any() ) ).thenReturn( nodesInfo );
        Mockito.when( clusterAdminClient.health( Mockito.any() ) ).thenReturn( clusterHealthInfo );

        this.clusterState = Mockito.mock( ClusterState.class );
        this.localNodeInfo = Mockito.mock( NodeInfo.class );
        final ClusterStateResponse clusterStateResponse = Mockito.mock( ClusterStateResponse.class );
        final NodesInfoResponse nodesInfoResponse = Mockito.mock( NodesInfoResponse.class );
        final ClusterHealthResponse clusterHealthResponse = Mockito.mock( ClusterHealthResponse.class );

        Mockito.when( clusterStateInfo.actionGet() ).thenReturn( clusterStateResponse );
        Mockito.when( nodesInfo.actionGet() ).thenReturn( nodesInfoResponse );
        Mockito.when( clusterHealthInfo.actionGet() ).thenReturn( clusterHealthResponse );

        final ClusterName clusterName = new ClusterName( "clusterName" );
        Mockito.when( clusterState.getClusterName() ).thenReturn( clusterName );
        Mockito.when( clusterStateResponse.getClusterName() ).thenReturn( clusterName );

        Mockito.when( clusterStateResponse.getState() ).thenReturn( clusterState );
        Mockito.when( nodesInfoResponse.getAt( 0 ) ).thenReturn( localNodeInfo );
        Mockito.when( clusterHealthResponse.getStatus() ).thenReturn( ClusterHealthStatus.GREEN );

        final ClusterHealthProvider clusterHealthProvider = new ClusterHealthProvider();
        clusterHealthProvider.setClusterAdminClient( clusterAdminClient );
        final ClusterStateProvider clusterStateProvider = new ClusterStateProvider();
        clusterStateProvider.setClusterAdminClient( clusterAdminClient );

        clusterReporter.setClusterHealthProvider( clusterHealthProvider );
        clusterReporter.setClusterStateProvider( clusterStateProvider );
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
        Mockito.when( localNodeInfo.getNode() ).thenReturn( node1 );

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
    public void testLocalNodeInfo_Exception()
        throws Exception
    {
        Mockito.when( nodesInfo.actionGet() ).thenThrow( new ElasticsearchException( "local node info exception" ) );

        final DiscoveryNode node1 =
            new DiscoveryNode( "nodeName", "nodeId", "hostName", "hostAddress", new LocalTransportAddress( "10.10.10.1" ), new HashMap<>(),
                               Version.fromString( "1.0.0" ) );

        final DiscoveryNodes nodes = DiscoveryNodes.builder().
            put( node1 ).
            localNodeId( node1.getId() ).
            build();

        Mockito.when( clusterState.getNodes() ).thenReturn( nodes );
        Mockito.when( localNodeInfo.getNode() ).thenReturn( node1 );

        assertJson( "local_node_info_exception.json", clusterReporter.getReport().toString() );
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
        Mockito.when( localNodeInfo.getNode() ).thenReturn( node1 );

        assertJson( "cluster_health_info_exception.json", clusterReporter.getReport().toString() );
    }

    @Test
    public void cluster_with_two_nodes()
        throws Exception
    {
        final DiscoveryNode node1 =
            new DiscoveryNode( "nodeName1", "nodeId1", "hostName1", "hostAddress1", new LocalTransportAddress( "10.10.10.1" ),
                               new HashMap<>(), Version.fromString( "1.0.0" ) );

        final DiscoveryNode node2 =
            new DiscoveryNode( "nodeName2", "nodeId2", "hostName2", "hostAddress2", new LocalTransportAddress( "10.10.10.2" ),
                               new HashMap<>(), Version.fromString( "1.0.0" ) );

        final DiscoveryNodes nodes = DiscoveryNodes.builder().
            put( node1 ).
            put( node2 ).
            localNodeId( node1.getId() ).
            masterNodeId( node2.getId() ).
            build();

        Mockito.when( clusterState.getNodes() ).thenReturn( nodes );
        Mockito.when( localNodeInfo.getNode() ).thenReturn( node1 );

        assertJson( "cluster_with_two_nodes.json", clusterReporter.getReport().toString() );
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

    private JsonNode parseJson( final String json )
        throws Exception
    {
        final ObjectMapper mapper = createObjectMapper();
        return mapper.readTree( json );
    }

    private String readFromFile( final String fileName )
        throws Exception
    {
        final URL url = getClass().getResource( fileName );
        if ( url == null )
        {
            throw new IllegalArgumentException( "Resource file [" + fileName + "] not found" );
        }

        return Resources.toString( url, Charsets.UTF_8 );
    }

    private String toJson( final Object value )
        throws Exception
    {
        final ObjectMapper mapper = createObjectMapper();
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString( value );
    }

    private ObjectMapper createObjectMapper()
    {
        final ObjectMapper mapper = new ObjectMapper();
        mapper.disable( SerializationFeature.FAIL_ON_EMPTY_BEANS );
        mapper.enable( MapperFeature.SORT_PROPERTIES_ALPHABETICALLY );
        mapper.enable( SerializationFeature.WRITE_NULL_MAP_VALUES );
        mapper.setSerializationInclusion( JsonInclude.Include.ALWAYS );
        return mapper;
    }

    private final void assertEquals( Object a1, Object a2 )
    {
        Assert.assertEquals( a1, a2 );
    }


}
