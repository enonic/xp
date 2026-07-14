package com.enonic.xp.portal.impl.main;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.portal.script.PortalScriptService;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.script.ScriptExports;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
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

    @BeforeEach
    void setup()
    {
        this.executor = new MainExecutor( this.scriptService );
    }

    @Test
    void mainJsMissing()
    {
        final Application app = mock( Application.class );
        when( app.getKey() ).thenReturn( ApplicationKey.from( "foo.bar" ) );

        this.executor.activated( app );

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

        this.executor.activated( app );
    }

    @Test
    void mainJsExecute()
    {
        final ResourceKey key = ResourceKey.from( "foo.bar:/main.js" );
        when( this.scriptService.hasScript( key ) ).thenReturn( true );
        when( this.scriptService.executeAsync( key ) ).thenReturn( CompletableFuture.completedFuture( null ) );

        final Application app = mock( Application.class );
        when( app.getKey() ).thenReturn( ApplicationKey.from( "foo.bar" ) );

        this.executor.activated( app );
    }

    @Test
    void awaitBootstrapped_unknownApp_returnsImmediately()
    {
        this.executor.awaitBootstrapped( ApplicationKey.from( "never.activated" ) );
    }

    @Test
    void awaitBootstrapped_blocksUntilMainJsCompletes()
        throws Exception
    {
        final ApplicationKey appKey = ApplicationKey.from( "foo.bar" );
        final ResourceKey key = ResourceKey.from( "foo.bar:/main.js" );
        final CompletableFuture<ScriptExports> mainJs = new CompletableFuture<>();
        when( this.scriptService.hasScript( key ) ).thenReturn( true );
        when( this.scriptService.executeAsync( key ) ).thenReturn( mainJs );

        final Application app = mock( Application.class );
        when( app.getKey() ).thenReturn( appKey );
        this.executor.activated( app );

        final CountDownLatch awaiting = new CountDownLatch( 1 );
        final Thread controller = new Thread( () -> {
            awaiting.countDown();
            this.executor.awaitBootstrapped( appKey );
        } );
        controller.start();

        assertTrue( awaiting.await( 10, TimeUnit.SECONDS ) );
        // the controller thread is gated while main.js has not completed
        controller.join( 300 );
        assertTrue( controller.isAlive() );

        mainJs.complete( null );
        controller.join( TimeUnit.SECONDS.toMillis( 10 ) );
        assertFalse( controller.isAlive() );

        // completed bootstrap: subsequent awaits return immediately
        this.executor.awaitBootstrapped( appKey );
    }

    @Test
    void awaitBootstrapped_opensOnMainJsFailure()
    {
        final ApplicationKey appKey = ApplicationKey.from( "foo.bar" );
        final ResourceKey key = ResourceKey.from( "foo.bar:/main.js" );
        when( this.scriptService.hasScript( key ) ).thenReturn( true );
        when( this.scriptService.executeAsync( key ) ).thenReturn( CompletableFuture.failedFuture( new RuntimeException() ) );

        final Application app = mock( Application.class );
        when( app.getKey() ).thenReturn( appKey );
        this.executor.activated( app );

        // a broken main.js must not dam the application
        this.executor.awaitBootstrapped( appKey );
    }

    @Test
    void deactivated_releasesWaiters()
        throws Exception
    {
        final ApplicationKey appKey = ApplicationKey.from( "foo.bar" );
        final ResourceKey key = ResourceKey.from( "foo.bar:/main.js" );
        when( this.scriptService.hasScript( key ) ).thenReturn( true );
        when( this.scriptService.executeAsync( key ) ).thenReturn( new CompletableFuture<>() );

        final Application app = mock( Application.class );
        when( app.getKey() ).thenReturn( appKey );
        this.executor.activated( app );

        final Thread controller = new Thread( () -> this.executor.awaitBootstrapped( appKey ) );
        controller.start();

        this.executor.deactivated( app );

        controller.join( TimeUnit.SECONDS.toMillis( 10 ) );
        assertFalse( controller.isAlive() );
    }
}
