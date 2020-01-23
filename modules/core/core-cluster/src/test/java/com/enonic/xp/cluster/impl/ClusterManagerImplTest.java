package com.enonic.xp.cluster.impl;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import com.enonic.xp.cluster.ClusterHealth;
import com.enonic.xp.cluster.ClusterId;
import com.enonic.xp.cluster.ClusterNode;
import com.enonic.xp.cluster.ClusterNodes;
import com.enonic.xp.cluster.ClusterState;
import com.enonic.xp.cluster.Clusters;
import com.enonic.xp.core.internal.concurrent.RecurringJob;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@Disabled
class ClusterManagerImplTest
{
    private ClusterManagerImpl clusterManager;

    private ClusterCheckSchedulerMock clusterCheckSchedulerMock;

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
        clusterManager.activate();

        assertActive( provider );
        clusterManager.getClusterState();
        assertActive( provider );

        provider.setHealth( ClusterHealth.red() );
        clusterCheckSchedulerMock.rerun();
        assertClusterError();
        assertDeactivated( provider );

        provider.setHealth( ClusterHealth.green() );
        clusterCheckSchedulerMock.rerun();
        assertClusterOk();
        assertActive( provider );
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
        clusterManager.activate();

        assertActive( provider1, provider2 );

        provider1.setHealth( ClusterHealth.red() );
        clusterCheckSchedulerMock.rerun();
        assertClusterError();
        assertDeactivated( provider1, provider2 );
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
        clusterManager.activate();

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
        clusterManager.activate();

        assertClusterOk();

        this.clusterManager.removeProvider( provider2 );

        assertClusterError();
    }

    @Test
    void activate_deactivate_task_canceled()
    {
        createManager( "elasticsearch" );

        clusterManager.activate();
        clusterManager.deactivate();
        clusterCheckSchedulerMock.verifyStopped();
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
        clusterCheckSchedulerMock = new ClusterCheckSchedulerMock();
        clusterManager = new ClusterManagerImpl( clusterCheckSchedulerMock, clusters );
    }

    private void assertActive( final TestCluster... providers )
    {
        assertAll( Stream.of( providers ).
            map( provider -> (Executable) () -> assertTrue( provider.isEnabled(),
                                                            String.format( "Provider '%s' not active", provider.getId() ) ) ) );
    }

    private void assertDeactivated( final TestCluster... providers )
    {
        assertAll( Stream.of( providers ).
            map( provider -> (Executable) () -> assertFalse( provider.isEnabled(),
                                                             String.format( "Provider '%s' not deactivated", provider.getId() ) ) ) );
    }

    private static class ClusterCheckSchedulerMock
        implements ClusterCheckScheduler
    {
        private Runnable command;

        private RecurringJob scheduledFutureMock;

        @Override
        public RecurringJob scheduleWithFixedDelay( final Runnable command )
        {
            this.command = command;
            command.run();
            scheduledFutureMock = mock( RecurringJob.class );
            return scheduledFutureMock;
        }

        public void rerun()
        {
            command.run();
        }

        public void verifyStopped()
        {
            verify( scheduledFutureMock ).cancel();
        }
    }
}