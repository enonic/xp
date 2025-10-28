package com.enonic.xp.portal.impl.main;

import java.util.concurrent.CompletableFuture;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.portal.script.PortalScriptService;
import com.enonic.xp.resource.ResourceKey;

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
}
