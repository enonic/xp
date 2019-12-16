package com.enonic.xp.cluster.impl.report;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.fasterxml.jackson.databind.JsonNode;

import com.enonic.xp.cluster.Cluster;
import com.enonic.xp.cluster.ClusterHealth;
import com.enonic.xp.cluster.ClusterId;
import com.enonic.xp.cluster.ClusterManager;
import com.enonic.xp.cluster.ClusterNode;
import com.enonic.xp.cluster.ClusterNodes;
import com.enonic.xp.cluster.ClusterState;
import com.enonic.xp.cluster.Clusters;
import com.enonic.xp.cluster.impl.TestCluster;
import com.enonic.xp.support.JsonTestHelper;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ClusterManagerReporterTest
{
    private ClusterManager clusterManager;

    @BeforeEach
    public void setUp()
        throws Exception
    {
        this.clusterManager = Mockito.mock( ClusterManager.class );

        final Cluster cluster1 = createCluster( ClusterId.from( "cluster1" ) );
        final Cluster cluster2 = createCluster( ClusterId.from( "cluster2" ) );

        final Clusters clusters = new Clusters( List.of( cluster1.getId(), cluster2.getId() ) );
        clusters.add( cluster1 );
        clusters.add( cluster2 );

        Mockito.when( this.clusterManager.getClusterState() ).thenReturn( ClusterState.OK );

        Mockito.when( this.clusterManager.getClusters() ).
            thenReturn( clusters );
    }

    @Test
    public void report()
        throws Exception
    {

        final ClusterManagerReporter reporter = new ClusterManagerReporter();
        reporter.setClusterManager( this.clusterManager );

        final JsonNode result = reporter.getReport();

        assertEquals( "cluster.manager", reporter.getName() );

        assertJson( "report.json", result );
    }

    private Cluster createCluster( final ClusterId cluster1Id )
    {
        return TestCluster.create().
            health( ClusterHealth.green() ).
            id( cluster1Id ).
            nodes( ClusterNodes.create().
                add( ClusterNode.from( "node1" ) ).
                add( ClusterNode.from( "node2" ) ).
                build() ).
            build();
    }

    private void assertJson( final String fileName, final JsonNode json )
    {
        final JsonTestHelper jsonTestHelper = new JsonTestHelper( this );
        final JsonNode jsonFromFile = jsonTestHelper.loadTestJson( fileName );
        jsonTestHelper.assertJsonEquals( jsonFromFile, json );
    }
}
