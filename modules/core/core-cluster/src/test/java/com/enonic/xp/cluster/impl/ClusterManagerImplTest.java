package com.enonic.xp.cluster.impl;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;

import com.enonic.xp.cluster.ClusterHealth;
import com.enonic.xp.cluster.ClusterNode;
import com.enonic.xp.cluster.ClusterNodes;
import com.enonic.xp.cluster.ClusterProviderHealth;
import com.enonic.xp.cluster.ClusterProviderId;

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

        final TestClusterProvider provider = TestClusterProvider.create().
            health( ClusterProviderHealth.GREEN ).
            id( ClusterProviderId.from( "elasticsearch" ) ).
            build();

        this.clusterManager.addProvider( provider );
        assertActive( provider );
        this.clusterManager.getHealth();
        assertActive( provider );
        provider.setHealth( ClusterProviderHealth.RED );
        Assert.assertEquals( ClusterHealth.ERROR, this.clusterManager.getHealth() );
        assertDeactivated( provider );
        provider.setHealth( ClusterProviderHealth.GREEN );
        Assert.assertEquals( ClusterHealth.OK, this.clusterManager.getHealth() );
        assertActive( provider );
    }

    @Test
    public void multiple_providers_life_cycle()
        throws Exception
    {
        createManager( "elasticsearch", "another" );

        final TestClusterProvider provider1 = TestClusterProvider.create().
            health( ClusterProviderHealth.GREEN ).
            id( ClusterProviderId.from( "elasticsearch" ) ).
            build();

        final TestClusterProvider provider2 = TestClusterProvider.create().
            health( ClusterProviderHealth.GREEN ).
            id( ClusterProviderId.from( "another" ) ).
            build();

        this.clusterManager.addProvider( provider1 );
        assertDeactivated( provider1 );

        this.clusterManager.addProvider( provider2 );
        assertActive( provider1, provider2 );

        provider1.setHealth( ClusterProviderHealth.RED );
        Assert.assertEquals( ClusterHealth.ERROR, clusterManager.getHealth() );
        assertDeactivated( provider1, provider2 );
    }

    @Test
    public void multiple_providers_nodes_mismatch()
        throws Exception
    {
        final TestClusterProvider provider1 = TestClusterProvider.create().
            health( ClusterProviderHealth.GREEN ).
            id( ClusterProviderId.from( "elasticsearch" ) ).
            nodes( ClusterNodes.create().
                add( ClusterNode.from( "a" ) ).
                build() ).
            build();

        final TestClusterProvider provider2 = TestClusterProvider.create().
            health( ClusterProviderHealth.GREEN ).
            id( ClusterProviderId.from( "another" ) ).nodes( ClusterNodes.create().
            add( ClusterNode.from( "a" ) ).
            build() ).
            build();

        createManager( "elasticsearch", "another" );

        this.clusterManager.addProvider( provider1 );
        this.clusterManager.addProvider( provider2 );
        Assert.assertEquals( ClusterHealth.OK, this.clusterManager.getHealth() );
        assertActive( provider1, provider2 );

        provider1.setNodes( ClusterNodes.create().
            add( ClusterNode.from( "a" ) ).
            add( ClusterNode.from( "b" ) ).
            build() );

        Assert.assertEquals( ClusterHealth.ERROR, this.clusterManager.getHealth() );
        assertDeactivated( provider1, provider2 );
    }

    private void createManager( final String... required )
    {
        List<ClusterProviderId> requiredIds = Lists.newArrayList();

        for ( final String req : required )
        {
            requiredIds.add( ClusterProviderId.from( req ) );
        }

        this.clusterManager = ClusterManagerImpl.create().
            checkIntervalMs( 0L ).
            requiredProviders( new ClusterProviders( requiredIds ) ).
            build();

    }

    private void assertActive( final TestClusterProvider... providers )
    {
        for ( final TestClusterProvider provider : providers )
        {
            assertTrue( String.format( "Provider '%s' not active", provider.getId() ), provider.isActive() );
        }
    }

    private void assertDeactivated( final TestClusterProvider... providers )
    {
        for ( final TestClusterProvider provider : providers )
        {
            Assert.assertFalse( String.format( "Provider '%s' not deactivated", provider.getId() ), provider.isActive() );
        }
    }
}