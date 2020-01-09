package com.enonic.xp.elasticsearch.impl.status.index;

import java.util.Arrays;

import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.admin.cluster.state.ClusterStateResponse;
import org.elasticsearch.client.AdminClient;
import org.elasticsearch.client.ClusterAdminClient;
import org.elasticsearch.cluster.ClusterState;
import org.elasticsearch.cluster.node.DiscoveryNode;
import org.elasticsearch.cluster.node.DiscoveryNodes;
import org.elasticsearch.cluster.routing.RecoverySource;
import org.elasticsearch.cluster.routing.RoutingTable;
import org.elasticsearch.cluster.routing.ShardRouting;
import org.elasticsearch.cluster.routing.ShardRoutingState;
import org.elasticsearch.cluster.routing.UnassignedInfo;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.index.shard.ShardId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.fasterxml.jackson.databind.JsonNode;

import com.enonic.xp.status.JsonStatusReporterTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Disabled
public class IndexReporterTest
    extends JsonStatusReporterTest
{
    private ClusterAdminClient clusterAdminClient;

    private IndexReporter indexReporter;

    @BeforeEach
    public void setUp()
    {

        clusterAdminClient = Mockito.mock( ClusterAdminClient.class );

        final AdminClient adminClient = Mockito.mock( AdminClient.class );
        Mockito.when( adminClient.cluster() ).thenReturn( clusterAdminClient );

        final IndexReportProvider indexReportProvider = new IndexReportProvider();
        //indexReportProvider.setAdminClient( adminClient );

        indexReporter = new IndexReporter();
        indexReporter.setIndexReportProvider( indexReportProvider );
    }

    @Test
    public void testGetReport()
        throws Exception
    {
        Mockito.doAnswer( invocation -> {
            final RecoverySource restoreResource = null;
            final UnassignedInfo unassignedInfo = new UnassignedInfo( UnassignedInfo.Reason.INDEX_CREATED, "" );
            final ShardRouting shardRouting = ShardRouting.newUnassigned( ShardId.fromString( "[myindex][0]" ), false, restoreResource, unassignedInfo );

            final RoutingTable routingTable = Mockito.mock( RoutingTable.class );
            Mockito.when( routingTable.shardsWithState( ShardRoutingState.STARTED ) ).thenReturn( Arrays.asList( shardRouting ) );

            final ClusterState clusterState = Mockito.mock( ClusterState.class );
            Mockito.when( clusterState.getRoutingTable() ).thenReturn( routingTable );

            // Mock discoveryNode
            DiscoveryNode discoveryNode = Mockito.mock( DiscoveryNode.class );

            final TransportAddress transportAddress = Mockito.mock( TransportAddress.class );
            Mockito.when( transportAddress.toString() ).thenReturn( "hostAddress" );

            Mockito.when( discoveryNode.getAddress() ).thenReturn( transportAddress );
            Mockito.when( discoveryNode.getId() ).thenReturn( "hostId" );

            // Mock discoveryNodes
            DiscoveryNodes discoveryNodes = Mockito.mock( DiscoveryNodes.class );
            Mockito.when( discoveryNodes.get( Mockito.any() ) ).thenReturn( discoveryNode );

            // Mock clusterState.getNodes()
            Mockito.when( clusterState.getNodes() ).thenReturn( discoveryNodes );

            // Mock getState()
            final ClusterStateResponse clusterStateResponse = Mockito.mock( ClusterStateResponse.class );
            Mockito.when( clusterStateResponse.getState() ).thenReturn( clusterState );

            ActionListener<ClusterStateResponse> listener = (ActionListener<ClusterStateResponse>) invocation.getArguments()[2];
            listener.onResponse( clusterStateResponse );
            return null;
        } ).when( clusterAdminClient ).
            execute( Mockito.any(), Mockito.any(), Mockito.any() );

        assertEquals( "index", indexReporter.getName() );
        final JsonNode report = indexReporter.getReport();
        assertEquals( parseJson( readFromFile( "index_report.json" ) ), report );
    }

    @Test
    public void testGetReportWithError()
        throws Exception
    {
        Mockito.doAnswer( invocation -> null ).
            when( clusterAdminClient ).
            state( Mockito.any(), Mockito.any() );
        assertEquals( "index", indexReporter.getName() );
        final JsonNode report = indexReporter.getReport();
        assertEquals( parseJson( readFromFile( "index_report_failed.json" ) ), report );
    }
}
