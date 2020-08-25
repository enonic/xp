package com.enonic.xp.portal.impl.main;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.portal.script.PortalScriptService;
import com.enonic.xp.resource.ResourceKey;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MainExecutorTest
{
    private MainExecutor executor;

    private PortalScriptService scriptService;

    @BeforeEach
    public void setup()
    {
        this.scriptService = mock( PortalScriptService.class );
        this.executor = new MainExecutor( this.scriptService );
    }

    @Test
    public void mainJsMissing()
    {
        final Application app = mock( Application.class );
        when( app.getKey() ).thenReturn( ApplicationKey.from( "foo.bar" ) );

        this.executor.activated( app );

        verify( this.scriptService, times( 1 ) ).hasScript( Mockito.any() );
        verify( this.scriptService, times( 0 ) ).execute( Mockito.any() );
    }

    @Test
    public void mainJsError()
    {
        final ResourceKey key = ResourceKey.from( "foo.bar:/main.js" );
        Mockito.when( this.scriptService.hasScript( key ) ).thenReturn( true );
        Mockito.when( this.scriptService.execute( key ) ).thenThrow( new RuntimeException() );

        final Application app = mock( Application.class );
        when( app.getKey() ).thenReturn( ApplicationKey.from( "foo.bar" ) );

        this.executor.activated( app );
    }

    @Test
    public void mainJsExecute()
    {
        final ResourceKey key = ResourceKey.from( "foo.bar:/main.js" );
        Mockito.when( this.scriptService.hasScript( key ) ).thenReturn( true );
        Mockito.when( this.scriptService.execute( key ) ).thenReturn( null );

        final Application app = mock( Application.class );
        when( app.getKey() ).thenReturn( ApplicationKey.from( "foo.bar" ) );

        this.executor.activated( app );
    }
}
