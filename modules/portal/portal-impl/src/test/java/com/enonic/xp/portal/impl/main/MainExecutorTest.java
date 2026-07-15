package com.enonic.xp.portal.impl.main;

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

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.doThrow;
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

    @BeforeEach
    void setup()
        throws Exception
    {
        // let the ServiceTracker construct and open against the mock registry (no initial services)
        lenient().when( this.bundleContext.createFilter( anyString() ) )
            .thenAnswer( invocation -> FrameworkUtil.createFilter( invocation.getArgument( 0 ) ) );
        lenient().when( this.bundleContext.getServiceReferences( anyString(), nullable( String.class ) ) ).thenReturn( null );

        // run the bootstrap synchronously on the caller thread so the test is deterministic
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
    void addingService_triggersBootstrap()
    {
        this.executor.addingService( appReference( "foo.bar" ) );

        verify( this.scriptService ).bootstrap( ResourceKey.from( "foo.bar:/main.js" ) );
    }

    @Test
    void bootstrapError_isSwallowed()
    {
        doThrow( new RuntimeException() ).when( this.scriptService ).bootstrap( ResourceKey.from( "foo.bar:/main.js" ) );

        this.executor.addingService( appReference( "foo.bar" ) );

        verify( this.scriptService, times( 1 ) ).bootstrap( ResourceKey.from( "foo.bar:/main.js" ) );
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
        this.executor.deactivate();
    }
}
