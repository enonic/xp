package com.enonic.xp.portal.impl.main;

import java.util.concurrent.CompletableFuture;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.portal.script.PortalScriptService;
import com.enonic.xp.resource.ResourceKey;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MainExecutorTest
{
    private static final ResourceKey MAIN_JS = ResourceKey.from( "foo.bar:/main.js" );

    @Mock
    private PortalScriptService scriptService;

    @Mock
    private BundleContext bundleContext;

    private MainExecutor executor;

    @BeforeEach
    void setup()
        throws Exception
    {
        // let the ServiceTracker construct and open against the mock registry (no initial services)
        lenient().when( this.bundleContext.createFilter( anyString() ) )
            .thenAnswer( invocation -> FrameworkUtil.createFilter( invocation.getArgument( 0 ) ) );
        lenient().when( this.bundleContext.getServiceReferences( anyString(), nullable( String.class ) ) ).thenReturn( null );

        this.executor = new MainExecutor( this.scriptService, this.bundleContext );
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
    void addingService_executesMain()
    {
        when( this.scriptService.hasScript( MAIN_JS ) ).thenReturn( true );
        when( this.scriptService.executeAsync( MAIN_JS ) ).thenReturn( CompletableFuture.completedFuture( null ) );

        this.executor.addingService( appReference( "foo.bar" ) );

        verify( this.scriptService ).executeAsync( MAIN_JS );
    }

    @Test
    void alreadyActiveApplication_isReplayedOnOpen()
        throws Exception
    {
        final ServiceReference<Application> reference = appReference( "foo.bar" );
        when( this.bundleContext.getServiceReferences( Application.class.getName(), null ) ).thenReturn(
            new ServiceReference[]{reference} );
        when( this.scriptService.hasScript( MAIN_JS ) ).thenReturn( true );
        when( this.scriptService.executeAsync( MAIN_JS ) ).thenReturn( CompletableFuture.completedFuture( null ) );

        assertDoesNotThrow( () -> new MainExecutor( this.scriptService, this.bundleContext ) );

        verify( this.scriptService ).executeAsync( MAIN_JS );
    }

    @Test
    void addingService_mainJsMissing()
    {
        when( this.scriptService.hasScript( MAIN_JS ) ).thenReturn( false );

        this.executor.addingService( appReference( "foo.bar" ) );

        verify( this.scriptService, never() ).executeAsync( any() );
    }

    @Test
    void addingService_mainJsError_isSwallowed()
    {
        when( this.scriptService.hasScript( MAIN_JS ) ).thenReturn( true );
        when( this.scriptService.executeAsync( MAIN_JS ) ).thenReturn( CompletableFuture.failedFuture( new RuntimeException() ) );

        assertDoesNotThrow( () -> this.executor.addingService( appReference( "foo.bar" ) ) );

        verify( this.scriptService ).executeAsync( MAIN_JS );
    }

    @Test
    @SuppressWarnings("unchecked")
    void addingService_vanishedService_isIgnored()
    {
        final ServiceReference<Application> reference = mock( ServiceReference.class );
        when( this.bundleContext.getService( reference ) ).thenReturn( null );

        assertNull( this.executor.addingService( reference ) );

        verify( this.scriptService, never() ).hasScript( any() );
    }

    @Test
    void removedService_ungetsService()
    {
        final ServiceReference<Application> reference = appReference( "foo.bar" );
        final Application application = this.executor.addingService( reference );

        this.executor.removedService( reference, application );

        verify( this.bundleContext ).ungetService( reference );
    }

    @Test
    void deactivate_closesTracker()
    {
        assertDoesNotThrow( () -> this.executor.deactivate() );
    }
}
