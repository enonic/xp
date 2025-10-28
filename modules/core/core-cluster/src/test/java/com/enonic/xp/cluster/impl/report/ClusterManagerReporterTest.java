package com.enonic.xp.cluster.impl.report;

import java.io.ByteArrayOutputStream;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.google.common.net.MediaType;

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

class ClusterManagerReporterTest
{
    JsonTestHelper jsonTestHelper = new JsonTestHelper( this );

    private ClusterManager clusterManager;

    @BeforeEach
    void setUp()
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
    void report()
        throws Exception
    {
        final ClusterManagerReporter reporter = new ClusterManagerReporter(this.clusterManager );

        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        reporter.report( outputStream );

        assertEquals( "cluster.manager", reporter.getName() );
        assertEquals( MediaType.JSON_UTF_8, reporter.getMediaType() );
        jsonTestHelper.assertJsonEquals( jsonTestHelper.loadTestJson( "report.json" ),
                                         jsonTestHelper.bytesToJson( outputStream.toByteArray() ) );
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
}
