package com.enonic.xp.elasticsearch.impl.status;

import java.net.URL;

import org.elasticsearch.Version;
import org.elasticsearch.action.ActionFuture;
import org.elasticsearch.action.admin.cluster.node.info.NodeInfo;
import org.elasticsearch.action.admin.cluster.node.info.NodesInfoResponse;
import org.elasticsearch.action.admin.cluster.state.ClusterStateResponse;
import org.elasticsearch.client.AdminClient;
import org.elasticsearch.client.Client;
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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import com.enonic.xp.jaxrs.impl.json.ObjectMapperHelper;


public class ClusterReporterTest
{

    private ClusterReporter clusterReporter = new ClusterReporter();

    private ClusterState clusterState;

    private ClusterStateResponse clusterStateResponse;

    private NodesInfoResponse nodesInfoResponse;

    private NodeInfo localNodeInfo;


    @Before
    public void setup()
        throws Exception
    {
        final Client client = Mockito.mock( Client.class );
        final AdminClient adminClient = Mockito.mock( AdminClient.class );
        final ClusterAdminClient clusterAdminClient = Mockito.mock( ClusterAdminClient.class );
        final ActionFuture<ClusterStateResponse> clusterStateInfo = Mockito.mock( ActionFuture.class );
        final ActionFuture<NodesInfoResponse> nodesInfo = Mockito.mock( ActionFuture.class );

        Mockito.when( client.admin() ).thenReturn( adminClient );
        Mockito.when( adminClient.cluster() ).thenReturn( clusterAdminClient );
        Mockito.when( clusterAdminClient.state( Mockito.any() ) ).thenReturn( clusterStateInfo );
        Mockito.when( clusterAdminClient.nodesInfo( Mockito.any() ) ).thenReturn( nodesInfo );

        this.clusterState = Mockito.mock( ClusterState.class );
        this.localNodeInfo = Mockito.mock( NodeInfo.class );
        this.clusterStateResponse = Mockito.mock( ClusterStateResponse.class );
        this.nodesInfoResponse = Mockito.mock( NodesInfoResponse.class );

        Mockito.when( clusterStateInfo.actionGet() ).thenReturn( clusterStateResponse );
        Mockito.when( nodesInfo.actionGet() ).thenReturn( nodesInfoResponse );

        final ClusterName clusterName = Mockito.mock( ClusterName.class );
        Mockito.when( clusterState.getClusterName() ).thenReturn( clusterName );
        Mockito.when( clusterName.toString() ).thenReturn( "ClusterName" );

        Mockito.when( clusterStateResponse.getState() ).thenReturn( clusterState );
        Mockito.when( nodesInfoResponse.getAt( 0 ) ).thenReturn( localNodeInfo );

        clusterReporter.setClient( client );
    }

    @Test
    public void testOneNodeCluster()
        throws Exception
    {
        final DiscoveryNode node1 = new DiscoveryNode( "first-node-id", new LocalTransportAddress( "10.10.10.1" ), Version.CURRENT );
        final DiscoveryNodes nodes = DiscoveryNodes.builder().
            put( node1 ).
            localNodeId( node1.getId() ).
            build();

        Mockito.when( clusterState.getNodes() ).thenReturn( nodes );
        Mockito.when( localNodeInfo.getNode() ).thenReturn( node1 );

        assertJson( "cluster_with_one_node.json", clusterReporter.getReport().toString() );
    }

    @Test
    public void testEmptyClusterState()
        throws Exception
    {
        Mockito.when( clusterStateResponse.getState() ).thenReturn( null );

        assertJson( "cluster_empty_state_error.json", clusterReporter.getReport().toString() );
    }

    @Test
    public void testEmptyLocalNodeInfo()
        throws Exception
    {
        Mockito.when( nodesInfoResponse.getAt( 0 ) ).thenReturn( null );

        assertJson( "cluster_with_empty_local_node_info_error.json", clusterReporter.getReport().toString() );
    }

    @Test
    public void testMasterNode()
        throws Exception
    {
        final DiscoveryNode node1 = new DiscoveryNode( "first-node-id", new LocalTransportAddress( "10.10.10.1" ), Version.CURRENT );
        final DiscoveryNode node2 = new DiscoveryNode( "second-node-id", new LocalTransportAddress( "10.10.10.2" ), Version.CURRENT );
        final DiscoveryNodes nodes = DiscoveryNodes.builder().
            put( node1 ).
            put( node2 ).
            localNodeId( node1.getId() ).
            masterNodeId( node2.getId() ).
            build();

        Mockito.when( clusterState.getNodes() ).thenReturn( nodes );
        Mockito.when( localNodeInfo.getNode() ).thenReturn( node1 );

        assertJson( "cluster_with_master_node.json", clusterReporter.getReport().toString() );
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
        final ObjectMapper mapper = ObjectMapperHelper.create();
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
        final ObjectMapper mapper = ObjectMapperHelper.create();
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString( value );
    }

    private final void assertEquals( Object a1, Object a2 )
    {
        Assert.assertEquals( a1, a2 );
    }


}
