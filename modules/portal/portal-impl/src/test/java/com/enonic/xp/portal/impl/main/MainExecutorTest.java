package com.enonic.xp.portal.impl.main;

import java.util.concurrent.CompletableFuture;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.osgi.service.condition.Condition;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.app.Applications;
import com.enonic.xp.portal.script.PortalScriptService;
import com.enonic.xp.resource.ResourceKey;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MainExecutorTest
{
    @Mock
    private PortalScriptService scriptService;

    @Mock
    private ApplicationService applicationService;

    @Mock
    private Condition deployReady;

    private MainExecutor newExecutor()
    {
        return new MainExecutor( this.scriptService, this.applicationService, this.deployReady );
    }

    @BeforeEach
    void setup()
    {
        lenient().when( this.applicationService.getInstalledApplications() ).thenReturn( Applications.empty() );
    }

    @Test
    void mainJsMissing()
    {
        final Application app = mock( Application.class );
        when( app.getKey() ).thenReturn( ApplicationKey.from( "foo.bar" ) );

        newExecutor().activated( app );

        verify( this.scriptService, times( 1 ) ).hasScript( any() );
        verify( this.scriptService, times( 0 ) ).execute( any() );
    }

    @Test
    void mainJsError()
    {
        final ResourceKey key = ResourceKey.from( "foo.bar:/main.js" );
        when( this.scriptService.hasScript( key ) ).thenReturn( true );
        when( this.scriptService.executeAsync( key ) ).thenReturn( CompletableFuture.failedFuture( new RuntimeException() ) );

        final Application app = mock( Application.class );
        when( app.getKey() ).thenReturn( ApplicationKey.from( "foo.bar" ) );

        newExecutor().activated( app );
    }

    @Test
    void mainJsExecute()
    {
        final ResourceKey key = ResourceKey.from( "foo.bar:/main.js" );
        when( this.scriptService.hasScript( key ) ).thenReturn( true );
        when( this.scriptService.executeAsync( key ) ).thenReturn( CompletableFuture.completedFuture( null ) );

        final Application app = mock( Application.class );
        when( app.getKey() ).thenReturn( ApplicationKey.from( "foo.bar" ) );

        newExecutor().activated( app );
    }

    @Test
    void alreadyStartedApplicationsExecutedOnActivation()
    {
        final Application started = mock( Application.class );
        when( started.getKey() ).thenReturn( ApplicationKey.from( "foo.started" ) );
        when( started.isStarted() ).thenReturn( true );

        final Application stopped = mock( Application.class );
        when( stopped.isStarted() ).thenReturn( false );

        when( this.applicationService.getInstalledApplications() ).thenReturn( Applications.from( started, stopped ) );

        newExecutor();

        verify( this.scriptService, times( 1 ) ).hasScript( ResourceKey.from( "foo.started:/main.js" ) );
        verify( this.scriptService, times( 0 ) ).hasScript( ResourceKey.from( "foo.stopped:/main.js" ) );
    }
}
