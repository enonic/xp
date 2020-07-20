package com.enonic.xp.cluster.impl;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import com.enonic.xp.cluster.ClusterHealth;
import com.enonic.xp.cluster.ClusterId;
import com.enonic.xp.cluster.ClusterNode;
import com.enonic.xp.cluster.ClusterNodes;
import com.enonic.xp.cluster.ClusterState;
import com.enonic.xp.cluster.Clusters;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ClusterManagerImplTest
{
    private ClusterManagerImpl clusterManager;

    @Test
    void single_provider_life_cycle()
    {
        createManager( "elasticsearch" );

        final TestCluster provider = TestCluster.create().
            health( ClusterHealth.green() ).
            id( ClusterId.from( "elasticsearch" ) ).
            nodes( ClusterNodes.create().
                add( ClusterNode.from( "a" ) ).
                build() ).
            build();

        clusterManager.addProvider( provider );

        clusterManager.getClusterState();

        provider.setHealth( ClusterHealth.red() );

        assertClusterError();

        provider.setHealth( ClusterHealth.green() );

        assertClusterOk();
    }

    @Test
    void multiple_providers_life_cycle()
    {
        createManager( "elasticsearch", "another" );

        final TestCluster provider1 = TestCluster.create().
            health( ClusterHealth.green() ).
            id( ClusterId.from( "elasticsearch" ) ).
            nodes( ClusterNodes.create().
                add( ClusterNode.from( "a" ) ).
                build() ).
            build();

        final TestCluster provider2 = TestCluster.create().
            health( ClusterHealth.green() ).
            id( ClusterId.from( "another" ) ).
            nodes( ClusterNodes.create().
                add( ClusterNode.from( "a" ) ).
                build() ).
            build();

        clusterManager.addProvider( provider1 );
        clusterManager.addProvider( provider2 );

        provider1.setHealth( ClusterHealth.red() );

        assertClusterError();
    }

    @Test
    void multiple_providers_nodes_mismatch()
    {
        final TestCluster provider1 = TestCluster.create().
            health( ClusterHealth.green() ).
            id( ClusterId.from( "elasticsearch" ) ).
            nodes( ClusterNodes.create().
                add( ClusterNode.from( "a" ) ).
                build() ).
            build();

        final TestCluster provider2 = TestCluster.create().
            health( ClusterHealth.green() ).
            id( ClusterId.from( "another" ) ).nodes( ClusterNodes.create().
            add( ClusterNode.from( "a" ) ).
            build() ).
            build();

        createManager( "elasticsearch", "another" );

        clusterManager.addProvider( provider1 );
        clusterManager.addProvider( provider2 );

        assertClusterOk();

        provider1.setNodes( ClusterNodes.create().
            add( ClusterNode.from( "a" ) ).
            add( ClusterNode.from( "b" ) ).
            build() );
        assertClusterOk();
    }

    @Test
    void fail_after_register()
    {
        final TestCluster provider1 = TestCluster.create().
            health( ClusterHealth.green() ).
            id( ClusterId.from( "elasticsearch" ) ).
            nodes( ClusterNodes.create().
                add( ClusterNode.from( "a" ) ).
                build() ).
            build();

        final TestCluster provider2 = TestCluster.create().
            health( ClusterHealth.green() ).
            id( ClusterId.from( "another" ) ).nodes( ClusterNodes.create().
            add( ClusterNode.from( "a" ) ).
            build() ).
            build();

        createManager( "elasticsearch", "another" );
        clusterManager.addProvider( provider1 );
        clusterManager.addProvider( provider2 );

        assertClusterOk();

        this.clusterManager.removeProvider( provider2 );

        assertClusterError();
    }

    private void assertClusterError()
    {
        assertEquals( ClusterState.ERROR, clusterManager.getClusterState() );
    }

    private void assertClusterOk()
    {
        assertEquals( ClusterState.OK, clusterManager.getClusterState() );
    }

    private void createManager( final String... required )
    {
        final Clusters clusters = new Clusters( Stream.of( required ).map( ClusterId::from ).collect( Collectors.toList() ) );
        clusterManager = new ClusterManagerImpl( clusters );
    }
}