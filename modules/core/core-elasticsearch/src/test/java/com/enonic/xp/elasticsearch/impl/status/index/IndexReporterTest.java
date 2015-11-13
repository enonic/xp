package com.enonic.xp.elasticsearch.impl.status.index;

import java.net.URL;
import java.util.Arrays;

import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.admin.cluster.state.ClusterStateResponse;
import org.elasticsearch.client.AdminClient;
import org.elasticsearch.client.ClusterAdminClient;
import org.elasticsearch.cluster.ClusterState;
import org.elasticsearch.cluster.routing.RoutingTable;
import org.elasticsearch.cluster.routing.ShardRouting;
import org.elasticsearch.cluster.routing.ShardRoutingState;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;


public class IndexReporterTest
{
    private ClusterAdminClient clusterAdminClient;

    private IndexReporter indexReporter;

    @Before
    public void setUp()
    {

        clusterAdminClient = Mockito.mock( ClusterAdminClient.class );
        Mockito.doAnswer( invocation -> {
            final ShardRouting shardRouting = Mockito.mock( ShardRouting.class );
            Mockito.when( shardRouting.index() ).thenReturn( "myindex" );
            Mockito.when( shardRouting.id() ).thenReturn( 0 );
            Mockito.when( shardRouting.primary() ).thenReturn( true );
            Mockito.when( shardRouting.index() ).thenReturn( "myindex" );
            Mockito.when( shardRouting.state() ).thenReturn( ShardRoutingState.STARTED );
            Mockito.when( shardRouting.currentNodeId() ).thenReturn( "nodeId" );

            final RoutingTable routingTable = Mockito.mock( RoutingTable.class );
            Mockito.when( routingTable.allShards() ).thenReturn( Arrays.asList( shardRouting ) );

            final ClusterState clusterState = Mockito.mock( ClusterState.class );
            Mockito.when( clusterState.getRoutingTable() ).thenReturn( routingTable );

            final ClusterStateResponse clusterStateResponse = Mockito.mock( ClusterStateResponse.class );
            Mockito.when( clusterStateResponse.getState() ).thenReturn( clusterState );
            ActionListener<ClusterStateResponse> listener = (ActionListener<ClusterStateResponse>) invocation.getArguments()[1];
            listener.onResponse( clusterStateResponse );
            return null;
        } ).when( clusterAdminClient ).
            state( Mockito.any(), Mockito.any() );

        final AdminClient adminClient = Mockito.mock( AdminClient.class );
        Mockito.when( adminClient.cluster() ).thenReturn( clusterAdminClient );

        final IndexReportProvider indexReportProvider = new IndexReportProvider();
        indexReportProvider.setAdminClient( adminClient );

        indexReporter = new IndexReporter();
        indexReporter.setIndexReportProvider( indexReportProvider );
    }

    @Test
    public void testGetReport()
        throws Exception
    {
        Assert.assertEquals( "index", indexReporter.getName() );
        final ObjectNode report = indexReporter.getReport();
        Assert.assertEquals( parseJson( readFromFile( "index_report.json" ) ), report );
    }

    @Test
    public void testGetReportWithError()
        throws Exception
    {
        Mockito.doAnswer( invocation -> null ).
            when( clusterAdminClient ).
            state( Mockito.any(), Mockito.any() );
        Assert.assertEquals( "index", indexReporter.getName() );
        final ObjectNode report = indexReporter.getReport();
        Assert.assertEquals( parseJson( readFromFile( "index_report_failed.json" ) ), report );
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

    private JsonNode parseJson( final String json )
        throws Exception
    {
        final ObjectMapper mapper = createObjectMapper();
        return mapper.readTree( json );
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

}
