package com.enonic.xp.portal.impl.main;

import java.util.Dictionary;
import java.util.concurrent.CompletableFuture;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.condition.Condition;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.portal.script.PortalScriptService;
import com.enonic.xp.resource.ResourceKey;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
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
    @SuppressWarnings("unchecked")
    void setup()
    {
        lenient().when( this.bundleContext.registerService( eq( Condition.class ), any(), any() ) ).thenReturn( this.registration );
        this.executor = new MainExecutor( this.scriptService, this.bundleContext );
    }

    private static Application app()
    {
        final Application app = mock( Application.class );
        when( app.getKey() ).thenReturn( ApplicationKey.from( "foo.bar" ) );
        return app;
    }

    @Test
    void mainJsMissing_bootstrapsImmediately()
    {
        final ResourceKey key = ResourceKey.from( "foo.bar:/main.js" );
        when( this.scriptService.hasScript( key ) ).thenReturn( false );

        this.executor.activated( app() );

        verify( this.scriptService, never() ).executeAsync( any() );
        final Dictionary<String, ?> props = captureRegisteredProps();
        assertEquals( AppBootstrapBarrierImpl.BOOTSTRAP_CONDITION_ID, props.get( Condition.CONDITION_ID ) );
        assertEquals( "foo.bar", props.get( AppBootstrapBarrierImpl.APPLICATION_PROPERTY ) );
    }

    @Test
    void mainJsExecute_bootstrapsOnCompletion()
    {
        final ResourceKey key = ResourceKey.from( "foo.bar:/main.js" );
        when( this.scriptService.hasScript( key ) ).thenReturn( true );
        when( this.scriptService.executeAsync( key ) ).thenReturn( CompletableFuture.completedFuture( null ) );

        this.executor.activated( app() );

        assertEquals( "foo.bar", captureRegisteredProps().get( AppBootstrapBarrierImpl.APPLICATION_PROPERTY ) );
    }

    @Test
    void mainJsError_stillBootstraps()
    {
        final ResourceKey key = ResourceKey.from( "foo.bar:/main.js" );
        when( this.scriptService.hasScript( key ) ).thenReturn( true );
        when( this.scriptService.executeAsync( key ) ).thenReturn( CompletableFuture.failedFuture( new RuntimeException() ) );

        this.executor.activated( app() );

        // a broken main.js must not leave the application un-bootstrapped
        verify( this.bundleContext ).registerService( eq( Condition.class ), eq( Condition.INSTANCE ), any() );
    }

    @Test
    void deactivated_unregistersTheCondition()
    {
        final ResourceKey key = ResourceKey.from( "foo.bar:/main.js" );
        when( this.scriptService.hasScript( key ) ).thenReturn( false );

        final Application app = app();
        this.executor.activated( app );
        this.executor.deactivated( app );

        verify( this.registration, times( 1 ) ).unregister();
    }

    @SuppressWarnings("unchecked")
    private Dictionary<String, ?> captureRegisteredProps()
    {
        final ArgumentCaptor<Dictionary<String, ?>> captor = ArgumentCaptor.forClass( Dictionary.class );
        verify( this.bundleContext ).registerService( eq( Condition.class ), eq( Condition.INSTANCE ), captor.capture() );
        return captor.getValue();
    }
}
