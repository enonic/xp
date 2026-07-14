package com.enonic.xp.portal.impl.main;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.osgi.service.condition.Condition;

import com.enonic.xp.app.ApplicationKey;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AppBootstrapBarrierImplTest
{
    private static final ApplicationKey APP = ApplicationKey.from( "foo.bar" );

    @Test
    @SuppressWarnings("unchecked")
    void returnsImmediatelyWhenConditionPresent()
        throws Exception
    {
        final BundleContext context = mock( BundleContext.class );
        when( context.getServiceReferences( eq( Condition.class ), anyString() ) ).thenReturn(
            List.of( mock( ServiceReference.class ) ) );

        new AppBootstrapBarrierImpl( context ).await( APP );

        // fast path: no tracker is opened when the bootstrap condition is already present
        verify( context, never() ).createFilter( anyString() );
    }

    @Test
    @SuppressWarnings("unchecked")
    void awaitsThroughTrackerWhenNotYetIndexed()
        throws Exception
    {
        final BundleContext context = mock( BundleContext.class );
        final ServiceReference<Condition> reference = mock( ServiceReference.class );

        // fast path misses, but the ServiceTracker finds the condition when it opens
        when( context.getServiceReferences( eq( Condition.class ), anyString() ) ).thenReturn( List.of() );
        when( context.createFilter( anyString() ) ).thenAnswer( invocation -> FrameworkUtil.createFilter( invocation.getArgument( 0 ) ) );
        when( context.getServiceReferences( nullable( String.class ), anyString() ) ).thenReturn(
            new ServiceReference[]{reference} );
        when( context.getService( reference ) ).thenReturn( Condition.INSTANCE );

        new AppBootstrapBarrierImpl( context ).await( APP );

        verify( context ).createFilter( anyString() );
        verify( context ).getService( reference );
    }
}
