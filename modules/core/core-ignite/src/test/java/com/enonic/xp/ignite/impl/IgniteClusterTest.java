package com.enonic.xp.ignite.impl;

import java.util.Collections;

import org.apache.ignite.Ignite;
import org.apache.ignite.cluster.ClusterNode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.enonic.xp.cluster.ClusterHealth;
import com.enonic.xp.cluster.ClusterId;
import com.enonic.xp.cluster.ClusterNodes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IgniteClusterTest
{
    @Mock
    private Ignite ignite;

    @Test
    void getId()
    {
        final IgniteCluster igniteCluster = new IgniteCluster( ignite );
        assertEquals( ClusterId.from( "ignite" ), igniteCluster.getId() );
    }

    @Test
    void getHealth()
    {
        final IgniteCluster igniteCluster = new IgniteCluster( ignite );
        assertEquals( ClusterHealth.green(), igniteCluster.getHealth() );
    }

    @Test
    void getNodes()
    {
        final IgniteCluster igniteCluster = new IgniteCluster( ignite );
        final org.apache.ignite.IgniteCluster igniteClusterMock = mock( org.apache.ignite.IgniteCluster.class );
        final ClusterNode clusterNode = mock( ClusterNode.class );

        when( igniteClusterMock.nodes() ).thenReturn( Collections.singleton( clusterNode ) );
        when( clusterNode.consistentId() ).thenReturn( "veryConsistentId" );
        when( ignite.cluster() ).thenReturn( igniteClusterMock );
        final ClusterNodes expected = ClusterNodes.create().add( com.enonic.xp.cluster.ClusterNode.from( "veryConsistentId" ) ).build();
        assertEquals( expected, igniteCluster.getNodes() );
    }

    @Test
    void clusterIsAlwaysOn()
    {
        final IgniteCluster igniteCluster = new IgniteCluster( ignite );
        igniteCluster.disable();
        assertTrue( igniteCluster.isEnabled() );
        igniteCluster.enable();
        assertTrue( igniteCluster.isEnabled() );
    }
}