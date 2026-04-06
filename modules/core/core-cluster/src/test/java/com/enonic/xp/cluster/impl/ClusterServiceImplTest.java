package com.enonic.xp.cluster.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.cluster.ClusterConfig;
import com.enonic.xp.cluster.ClusterService;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClusterServiceImplTest
{
    @Mock
    ClusterService clusteredClusterService;

    @Mock
    ClusterConfig clusterConfig;

    @Test
    void isLeader_noClusteredService_noConfig()
    {
        final ClusterServiceImpl clusterService = new ClusterServiceImpl();
        assertTrue( clusterService.isLeader() );
    }

    @Test
    void isLeader_app_noClusteredService_noConfig()
    {
        final ClusterServiceImpl clusterService = new ClusterServiceImpl();
        assertTrue( clusterService.isLeader( ApplicationKey.from( "com.example.myapp" ) ) );
    }

    @Test
    void isLeader_delegatesToClusteredService()
    {
        final ClusterServiceImpl clusterService = new ClusterServiceImpl();
        clusterService.setClusteredClusterService( clusteredClusterService );

        when( clusteredClusterService.isLeader() ).thenReturn( false );
        assertFalse( clusterService.isLeader() );

        when( clusteredClusterService.isLeader() ).thenReturn( true );
        assertTrue( clusterService.isLeader() );
    }

    @Test
    void isLeader_app_delegatesToClusteredService()
    {
        final ClusterServiceImpl clusterService = new ClusterServiceImpl();
        clusterService.setClusteredClusterService( clusteredClusterService );

        final ApplicationKey appKey = ApplicationKey.from( "com.example.myapp" );

        when( clusteredClusterService.isLeader( appKey ) ).thenReturn( false );
        assertFalse( clusterService.isLeader( appKey ) );

        when( clusteredClusterService.isLeader( appKey ) ).thenReturn( true );
        assertTrue( clusterService.isLeader( appKey ) );
    }

    @Test
    void isLeader_afterUnsetClusteredService()
    {
        final ClusterServiceImpl clusterService = new ClusterServiceImpl();
        clusterService.setClusteredClusterService( clusteredClusterService );
        clusterService.unsetClusteredClusterService( clusteredClusterService );

        assertTrue( clusterService.isLeader() );
    }

    @Test
    void isLeader_app_afterUnsetClusteredService()
    {
        final ClusterServiceImpl clusterService = new ClusterServiceImpl();
        clusterService.setClusteredClusterService( clusteredClusterService );
        clusterService.unsetClusteredClusterService( clusteredClusterService );

        assertTrue( clusterService.isLeader( ApplicationKey.from( "com.example.myapp" ) ) );
    }

    @Test
    void isLeader_noClusteredService_clusterDisabled()
    {
        final ClusterServiceImpl clusterService = new ClusterServiceImpl();
        when( clusterConfig.isEnabled() ).thenReturn( false );
        clusterService.setClusterConfig( clusterConfig );

        assertTrue( clusterService.isLeader() );
    }

    @Test
    void isLeader_noClusteredService_clusterEnabled_timesOut()
    {
        final ClusterServiceImpl clusterService = new ClusterServiceImpl();
        when( clusterConfig.isEnabled() ).thenReturn( true );
        clusterService.setClusterConfig( clusterConfig );

        assertThatThrownBy( clusterService::isLeader ).isInstanceOf( IllegalStateException.class )
            .hasMessage( "Cannot resolve cluster service" );
    }

    @Test
    void isLeader_app_noClusteredService_clusterEnabled_timesOut()
    {
        final ClusterServiceImpl clusterService = new ClusterServiceImpl();
        when( clusterConfig.isEnabled() ).thenReturn( true );
        clusterService.setClusterConfig( clusterConfig );

        assertThatThrownBy( () -> clusterService.isLeader( ApplicationKey.from( "com.example.myapp" ) ) ).isInstanceOf(
            IllegalStateException.class ).hasMessage( "Cannot resolve cluster service" );
    }

    @Test
    void isLeader_afterUnsetClusterConfig()
    {
        final ClusterServiceImpl clusterService = new ClusterServiceImpl();
        clusterService.setClusterConfig( clusterConfig );
        clusterService.unsetClusterConfig( clusterConfig );

        assertTrue( clusterService.isLeader() );
    }
}
