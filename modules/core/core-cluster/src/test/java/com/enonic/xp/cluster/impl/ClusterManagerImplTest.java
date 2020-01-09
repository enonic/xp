package com.enonic.xp.cluster.impl;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.enonic.xp.cluster.ClusterHealth;
import com.enonic.xp.cluster.ClusterId;
import com.enonic.xp.cluster.ClusterNode;
import com.enonic.xp.cluster.ClusterNodes;
import com.enonic.xp.cluster.ClusterState;
import com.enonic.xp.cluster.Clusters;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Disabled
class ClusterManagerImplTest
{
    private static final Duration CHECK_INTERVAL = Duration.ofMillis( 200 );

    private static final long TEST_INTERVAL_MILLS = CHECK_INTERVAL.multipliedBy( 2 ).toMillis();

    private ClusterManagerImpl clusterManager;

    @Test
    void single_provider_life_cycle()
        throws Exception
    {
        createManager( "elasticsearch" );

        final TestCluster provider = TestCluster.create().
            health( ClusterHealth.green() ).
            id( ClusterId.from( "elasticsearch" ) ).
            nodes( ClusterNodes.create().
                add( ClusterNode.from( "a" ) ).
                build() ).
            build();

        this.clusterManager.addProvider( provider );
        this.clusterManager.activate();
        Thread.sleep( TEST_INTERVAL_MILLS );

        assertActive( provider );
        this.clusterManager.getClusterState();
        assertActive( provider );

        provider.setHealth( ClusterHealth.red() );
        Thread.sleep( TEST_INTERVAL_MILLS );
        assertClusterError();
        assertDeactivated( provider );

        provider.setHealth( ClusterHealth.green() );
        Thread.sleep( TEST_INTERVAL_MILLS );
        assertClusterOk();
        assertActive( provider );
    }

    @Test
    void multiple_providers_life_cycle()
        throws Exception
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

        this.clusterManager.addProvider( provider1 );
        this.clusterManager.addProvider( provider2 );
        this.clusterManager.activate();
        Thread.sleep( TEST_INTERVAL_MILLS );

        assertActive( provider1, provider2 );

        provider1.setHealth( ClusterHealth.red() );
        Thread.sleep( TEST_INTERVAL_MILLS );
        assertClusterError();
        assertDeactivated( provider1, provider2 );
    }

    @Test
    void multiple_providers_nodes_mismatch()
        throws Exception
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

        this.clusterManager.addProvider( provider1 );
        this.clusterManager.addProvider( provider2 );
        this.clusterManager.activate();
        Thread.sleep( TEST_INTERVAL_MILLS );

        assertClusterOk();
        assertActive( provider1, provider2 );

        provider1.setNodes( ClusterNodes.create().
            add( ClusterNode.from( "a" ) ).
            add( ClusterNode.from( "b" ) ).
            build() );
        assertClusterOk();
    }

    @Test
    void fail_after_register()
        throws Exception
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
        this.clusterManager.addProvider( provider1 );
        this.clusterManager.addProvider( provider2 );
        this.clusterManager.activate();
        Thread.sleep( TEST_INTERVAL_MILLS );

        assertClusterOk();

        this.clusterManager.removeProvider( provider2 );

        assertClusterError();
    }

    private void assertClusterError()
    {
        assertEquals( ClusterState.ERROR, this.clusterManager.getClusterState() );
    }

    private void assertClusterOk()
    {
        assertEquals( ClusterState.OK, this.clusterManager.getClusterState() );
    }

    private void createManager( final String... required )
    {
        List<ClusterId> requiredIds = new ArrayList<>();

        for ( final String req : required )
        {
            requiredIds.add( ClusterId.from( req ) );
        }

        this.clusterManager = new ClusterManagerImpl( CHECK_INTERVAL, new Clusters( requiredIds ) );
    }

    private void assertActive( final TestCluster... providers )
    {
        for ( final TestCluster provider : providers )
        {
            assertTrue( provider.isEnabled(), String.format( "Provider '%s' not active", provider.getId() ) );
        }
    }

    private void assertDeactivated( final TestCluster... providers )
    {
        for ( final TestCluster provider : providers )
        {
            assertFalse( provider.isEnabled(), String.format( "Provider '%s' not deactivated", provider.getId() ) );
        }
    }
}