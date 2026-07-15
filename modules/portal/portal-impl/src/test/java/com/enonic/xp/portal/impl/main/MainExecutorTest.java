package com.enonic.xp.portal.impl.main;

import java.util.Dictionary;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.condition.Condition;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.portal.script.PortalScriptService;
import com.enonic.xp.resource.ResourceKey;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MainExecutorTest
{
    private MainExecutor executor;

    @Mock
    private PortalScriptService scriptService;

    @Mock
    private BundleContext bundleContext;

    @Mock
    private ServiceRegistration<Condition> registration;

    @BeforeEach
    void setup()
        throws Exception
    {
        // let the ServiceTracker construct and open against the mock registry (no initial services)
        lenient().when( this.bundleContext.createFilter( anyString() ) )
            .thenAnswer( invocation -> FrameworkUtil.createFilter( invocation.getArgument( 0 ) ) );
        lenient().when( this.bundleContext.getServiceReferences( anyString(), nullable( String.class ) ) ).thenReturn( null );
        lenient().when( this.bundleContext.registerService( eq( Condition.class ), eq( Condition.INSTANCE ), any() ) )
            .thenReturn( this.registration );

        // run main.js synchronously on the caller thread so the test is deterministic
        this.executor = new MainExecutor( this.scriptService, this.bundleContext, Runnable::run );
    }

    @SuppressWarnings("unchecked")
    private ServiceReference<Application> appReference( final String applicationKey )
    {
        final Application application = mock( Application.class );
        when( application.getKey() ).thenReturn( ApplicationKey.from( applicationKey ) );
        final ServiceReference<Application> reference = mock( ServiceReference.class );
        when( this.bundleContext.getService( reference ) ).thenReturn( application );
        return reference;
    }

    @Test
    void mainJsMissing_bootstrapsImmediately()
    {
        when( this.scriptService.hasScript( ResourceKey.from( "foo.bar:/main.js" ) ) ).thenReturn( false );

        this.executor.addingService( appReference( "foo.bar" ) );

        verify( this.scriptService, times( 0 ) ).execute( any() );
        final Dictionary<String, ?> props = captureRegisteredProps();
        assertEquals( AppBootstrapBarrierImpl.BOOTSTRAP_CONDITION_ID, props.get( Condition.CONDITION_ID ) );
        assertEquals( "foo.bar", props.get( AppBootstrapBarrierImpl.APPLICATION_PROPERTY ) );
    }

    @Test
    void mainJsExecute_bootstrapsOnCompletion()
    {
        when( this.scriptService.hasScript( ResourceKey.from( "foo.bar:/main.js" ) ) ).thenReturn( true );

        this.executor.addingService( appReference( "foo.bar" ) );

        verify( this.scriptService ).execute( ResourceKey.from( "foo.bar:/main.js" ) );
        assertEquals( "foo.bar", captureRegisteredProps().get( AppBootstrapBarrierImpl.APPLICATION_PROPERTY ) );
    }

    @Test
    void mainJsError_stillBootstraps()
    {
        when( this.scriptService.hasScript( ResourceKey.from( "foo.bar:/main.js" ) ) ).thenReturn( true );
        when( this.scriptService.execute( ResourceKey.from( "foo.bar:/main.js" ) ) ).thenThrow( new RuntimeException() );

        this.executor.addingService( appReference( "foo.bar" ) );

        // a broken main.js must not leave the application un-bootstrapped
        verify( this.bundleContext ).registerService( eq( Condition.class ), eq( Condition.INSTANCE ), any() );
    }

    @Test
    void removedService_unregistersTheCondition()
    {
        when( this.scriptService.hasScript( ResourceKey.from( "foo.bar:/main.js" ) ) ).thenReturn( false );

        final ServiceReference<Application> reference = appReference( "foo.bar" );
        final Application application = this.executor.addingService( reference );

        this.executor.removedService( reference, application );

        verify( this.registration, times( 1 ) ).unregister();
        verify( this.bundleContext ).ungetService( reference );
    }

    @Test
    void deactivate_closesTracker()
    {
        this.executor.deactivate();
    }

    @SuppressWarnings("unchecked")
    private Dictionary<String, ?> captureRegisteredProps()
    {
        final ArgumentCaptor<Dictionary<String, ?>> captor = ArgumentCaptor.forClass( Dictionary.class );
        verify( this.bundleContext ).registerService( eq( Condition.class ), eq( Condition.INSTANCE ), captor.capture() );
        return captor.getValue();
    }
}
