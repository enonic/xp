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

        new AppBootstrapBarrierImpl( context, 50 ).await( APP );

        // fast path: no tracker is opened when the bootstrap condition is already present
        verify( context, never() ).createFilter( anyString() );
    }

    @Test
    void failsOpenWhenConditionNeverAppears()
        throws Exception
    {
        final BundleContext context = mock( BundleContext.class );
        when( context.getServiceReferences( eq( Condition.class ), anyString() ) ).thenReturn( List.of() );
        when( context.createFilter( anyString() ) ).thenAnswer( invocation -> FrameworkUtil.createFilter( invocation.getArgument( 0 ) ) );
        when( context.getServiceReferences( nullable( String.class ), anyString() ) ).thenReturn( null );

        // the bounded wait elapses without the condition appearing: the caller proceeds, no throw
        new AppBootstrapBarrierImpl( context, 50 ).await( APP );

        verify( context ).createFilter( anyString() );
    }
}
