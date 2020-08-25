package com.enonic.xp.repo.impl.elasticsearch.snapshot;

import java.util.function.IntConsumer;

import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import com.enonic.xp.repo.impl.RepositoryEvents;

import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ClusterRestarterTest
{
    @Test
    void testLifecycle()
    {
        final BundleContext bundleContext = mock( BundleContext.class );
        final ServiceReference serviceReference = mock( ServiceReference.class );
        when( bundleContext.getServiceReference( "com.enonic.xp.launcher.impl.framework.FrameworkLifecycleService" ) ).thenReturn(
            serviceReference );
        final IntConsumer service = mock( IntConsumer.class );

        when( bundleContext.getService( serviceReference ) ).thenReturn( service );
        final ClusterRestarter clusterRestarter = new ClusterRestarter( bundleContext );

        clusterRestarter.onEvent( RepositoryEvents.restoreInitialized() );

        final InOrder inOrder = inOrder( service );
        inOrder.verify( service ).accept( 1 );

        clusterRestarter.onEvent( RepositoryEvents.restored() );

        inOrder.verify( service ).accept( 2 );
    }
}