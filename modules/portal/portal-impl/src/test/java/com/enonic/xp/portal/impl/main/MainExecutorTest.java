package com.enonic.xp.portal.impl.main;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.event.Event;
import com.enonic.xp.portal.script.PortalScriptService;
import com.enonic.xp.resource.ResourceKey;

public class MainExecutorTest
{
    private MainExecutor executor;

    private PortalScriptService scriptService;

    @Before
    public void setup()
    {
        this.scriptService = Mockito.mock( PortalScriptService.class );
        this.executor = new MainExecutor();
        this.executor.setScriptService( this.scriptService );
    }

    @Test
    public void wrongEvent()
    {
        this.executor.onEvent( Event.create( "other" ).build() );
        Mockito.verify( this.scriptService, Mockito.times( 0 ) ).execute( Mockito.any() );

        this.executor.onEvent( Event.create( "application" ).localOrigin( false ).build() );
        Mockito.verify( this.scriptService, Mockito.times( 0 ) ).execute( Mockito.any() );

        this.executor.onEvent( Event.create( "application" ).localOrigin( true ).build() );
        Mockito.verify( this.scriptService, Mockito.times( 0 ) ).execute( Mockito.any() );

        this.executor.onEvent( Event.create( "application" ).localOrigin( true ).value( "applicationKey", "foo.bar" ).build() );
        Mockito.verify( this.scriptService, Mockito.times( 0 ) ).execute( Mockito.any() );

        this.executor.onEvent( Event.create( "application" ).localOrigin( true ).value( "eventType", "STARTED" ).build() );
        Mockito.verify( this.scriptService, Mockito.times( 0 ) ).execute( Mockito.any() );
    }

    @Test
    public void mainJsMissing()
    {
        this.executor.onEvent( Event.create( "application" ).localOrigin( true ).
            value( "eventType", "STARTED" ).value( "applicationKey", "foo.bar" ).build() );

        Mockito.verify( this.scriptService, Mockito.times( 1 ) ).hasScript( Mockito.any() );
        Mockito.verify( this.scriptService, Mockito.times( 0 ) ).execute( Mockito.any() );
    }

    @Test
    public void mainJsError()
    {
        final ResourceKey key = ResourceKey.from( "foo.bar:/main.js" );
        Mockito.when( this.scriptService.hasScript( key ) ).thenReturn( true );
        Mockito.when( this.scriptService.execute( key ) ).thenThrow( new RuntimeException() );

        this.executor.onEvent( Event.create( "application" ).localOrigin( true ).
            value( "eventType", "STARTED" ).value( "applicationKey", "foo.bar" ).build() );
    }

    @Test
    public void mainJsExecute()
    {
        final ResourceKey key = ResourceKey.from( "foo.bar:/main.js" );
        Mockito.when( this.scriptService.hasScript( key ) ).thenReturn( true );
        Mockito.when( this.scriptService.execute( key ) ).thenReturn( null );

        this.executor.onEvent( Event.create( "application" ).localOrigin( true ).
            value( "eventType", "STARTED" ).value( "applicationKey", "foo.bar" ).build() );
    }
}
