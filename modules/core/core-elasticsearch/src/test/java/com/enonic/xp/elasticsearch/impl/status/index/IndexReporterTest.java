package com.enonic.xp.elasticsearch.impl.status.index;

import java.net.URL;

import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.admin.indices.stats.IndicesStatsResponse;
import org.elasticsearch.action.admin.indices.stats.ShardStats;
import org.elasticsearch.client.AdminClient;
import org.elasticsearch.client.IndicesAdminClient;
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
    private IndicesAdminClient indicesAdminClient;

    private IndexReporter indexReporter;

    @Before
    public void setUp()
    {

        indicesAdminClient = Mockito.mock( IndicesAdminClient.class );
        Mockito.doAnswer( invocation -> {
            final ShardRouting shardRouting = Mockito.mock( ShardRouting.class );
            Mockito.when( shardRouting.index() ).thenReturn( "myindex" );
            Mockito.when( shardRouting.id() ).thenReturn( 0 );
            Mockito.when( shardRouting.primary() ).thenReturn( true );
            Mockito.when( shardRouting.index() ).thenReturn( "myindex" );
            Mockito.when( shardRouting.state() ).thenReturn( ShardRoutingState.STARTED );
            Mockito.when( shardRouting.currentNodeId() ).thenReturn( "nodeId" );

            final ShardStats shardStats = Mockito.mock( ShardStats.class );
            Mockito.when( shardStats.getShardRouting() ).thenReturn( shardRouting );
            final ShardStats[] shardStatsArray = new ShardStats[]{shardStats};

            final IndicesStatsResponse indicesStatsResponse = Mockito.mock( IndicesStatsResponse.class );
            Mockito.when( indicesStatsResponse.getShards() ).thenReturn( shardStatsArray );
            ActionListener<IndicesStatsResponse> listener = (ActionListener<IndicesStatsResponse>) invocation.getArguments()[1];
            listener.onResponse( indicesStatsResponse );
            return null;
        } ).when( indicesAdminClient ).
            stats( Mockito.any(), Mockito.any() );

        final AdminClient adminClient = Mockito.mock( AdminClient.class );
        Mockito.when( adminClient.indices() ).thenReturn( indicesAdminClient );

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
            when( indicesAdminClient ).
            stats( Mockito.any(), Mockito.any() );
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
