package com.enonic.xp.cluster.impl;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;

import com.enonic.xp.cluster.ClusterHealth;
import com.enonic.xp.cluster.ClusterId;
import com.enonic.xp.cluster.ClusterNode;
import com.enonic.xp.cluster.ClusterNodes;
import com.enonic.xp.cluster.ClusterState;
import com.enonic.xp.cluster.Clusters;

import static junit.framework.TestCase.assertTrue;

public class ClusterManagerImplTest
{
    private ClusterManagerImpl clusterManager;

    @Before
    public void setUp()
        throws Exception
    {

    }

    @Test
    public void single_provider_life_cycle()
        throws Exception
    {
        createManager( "elasticsearch" );

        final TestCluster provider = TestCluster.create().
            health( ClusterHealth.GREEN ).
            id( ClusterId.from( "elasticsearch" ) ).
            nodes( ClusterNodes.create().
                add( ClusterNode.from( "a" ) ).
                build() ).
            build();

        this.clusterManager.addProvider( provider );
        assertActive( provider );
        this.clusterManager.getClusterState();
        assertActive( provider );
        provider.setHealth( ClusterHealth.RED );
        Assert.assertEquals( ClusterState.ERROR, this.clusterManager.getClusterState() );
        assertDeactivated( provider );
        provider.setHealth( ClusterHealth.GREEN );
        Assert.assertEquals( ClusterState.OK, this.clusterManager.getClusterState() );
        assertActive( provider );
    }

    @Test
    public void multiple_providers_life_cycle()
        throws Exception
    {
        createManager( "elasticsearch", "another" );

        final TestCluster provider1 = TestCluster.create().
            health( ClusterHealth.GREEN ).
            id( ClusterId.from( "elasticsearch" ) ).
            nodes( ClusterNodes.create().
                add( ClusterNode.from( "a" ) ).
                build() ).
            build();

        final TestCluster provider2 = TestCluster.create().
            health( ClusterHealth.GREEN ).
            id( ClusterId.from( "another" ) ).
            nodes( ClusterNodes.create().
                add( ClusterNode.from( "a" ) ).
                build() ).
            build();

        this.clusterManager.addProvider( provider1 );
        assertDeactivated( provider1 );

        this.clusterManager.addProvider( provider2 );
        assertActive( provider1, provider2 );

        provider1.setHealth( ClusterHealth.RED );
        Assert.assertEquals( ClusterState.ERROR, clusterManager.getClusterState() );
        assertDeactivated( provider1, provider2 );
    }

    @Test
    public void multiple_providers_nodes_mismatch()
        throws Exception
    {
        final TestCluster provider1 = TestCluster.create().
            health( ClusterHealth.GREEN ).
            id( ClusterId.from( "elasticsearch" ) ).
            nodes( ClusterNodes.create().
                add( ClusterNode.from( "a" ) ).
                build() ).
            build();

        final TestCluster provider2 = TestCluster.create().
            health( ClusterHealth.GREEN ).
            id( ClusterId.from( "another" ) ).nodes( ClusterNodes.create().
            add( ClusterNode.from( "a" ) ).
            build() ).
            build();

        createManager( "elasticsearch", "another" );

        this.clusterManager.addProvider( provider1 );
        this.clusterManager.addProvider( provider2 );
        Assert.assertEquals( ClusterState.OK, this.clusterManager.getClusterState() );
        assertActive( provider1, provider2 );

        provider1.setNodes( ClusterNodes.create().
            add( ClusterNode.from( "a" ) ).
            add( ClusterNode.from( "b" ) ).
            build() );

        Assert.assertEquals( ClusterState.ERROR, this.clusterManager.getClusterState() );
        assertDeactivated( provider1, provider2 );
    }

    private void createManager( final String... required )
    {
        List<ClusterId> requiredIds = Lists.newArrayList();

        for ( final String req : required )
        {
            requiredIds.add( ClusterId.from( req ) );
        }

        this.clusterManager = ClusterManagerImpl.create().
            checkIntervalMs( 0L ).
            requiredInstances( new Clusters( requiredIds ) ).
            build();

    }

    private void assertActive( final TestCluster... providers )
    {
        for ( final TestCluster provider : providers )
        {
            assertTrue( String.format( "Provider '%s' not active", provider.getId() ), provider.isEnabled() );
        }
    }

    private void assertDeactivated( final TestCluster... providers )
    {
        for ( final TestCluster provider : providers )
        {
            Assert.assertFalse( String.format( "Provider '%s' not deactivated", provider.getId() ), provider.isEnabled() );
        }
    }
}