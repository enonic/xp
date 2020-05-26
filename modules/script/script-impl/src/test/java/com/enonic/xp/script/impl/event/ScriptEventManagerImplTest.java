package com.enonic.xp.script.impl.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.google.common.collect.Lists;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationInvalidationLevel;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.event.Event;
import com.enonic.xp.script.event.ScriptEventListener;
import com.enonic.xp.script.impl.async.ScriptAsyncService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class ScriptEventManagerImplTest
{
    private ScriptEventManagerImpl manager;

    @BeforeEach
    public void setup()
    {
        final ScriptAsyncService scriptAsyncService = Mockito.mock( ScriptAsyncService.class );
        when( scriptAsyncService.getAsyncExecutor( any() ) ).thenReturn( Runnable::run );

        this.manager = new ScriptEventManagerImpl( scriptAsyncService );
    }

    private ScriptEventListener newListener( final String app )
    {
        final ScriptEventListener listener = Mockito.mock( ScriptEventListener.class );
        when( listener.getApplication() ).thenReturn( ApplicationKey.from( app ) );
        return listener;
    }

    @Test
    public void testIterable()
    {
        final ScriptEventListener listener = newListener( "foo.bar" );
        this.manager.add( listener );

        assertEquals( 1, Lists.newArrayList( this.manager ).size() );
        assertSame( listener, this.manager.iterator().next() );
    }

    @Test
    public void testInvalidate()
    {
        final ScriptEventListener listener1 = newListener( "foo.bar" );
        final ScriptEventListener listener2 = newListener( "foo.other" );
        this.manager.add( listener1 );
        this.manager.add( listener2 );

        assertEquals( 2, Lists.newArrayList( this.manager ).size() );

        Application application = Mockito.mock( Application.class );
        Mockito.when( application.getKey() ).thenReturn( ApplicationKey.from( "foo.bar" ) );

        this.manager.invalidate( ApplicationKey.from( "foo.bar" ), ApplicationInvalidationLevel.FULL );
        assertEquals( 1, Lists.newArrayList( this.manager ).size() );
    }

    @Test
    public void testOnEvent()
    {
        final ScriptEventListener listener1 = newListener( "foo.bar" );
        final ScriptEventListener listener2 = newListener( "foo.other" );
        this.manager.add( listener1 );
        this.manager.add( listener2 );

        final Event event = Event.create( "myEvent" ).build();
        this.manager.onEvent( event );

        Mockito.verify( listener1, Mockito.times( 1 ) ).onEvent( event );
        Mockito.verify( listener2, Mockito.times( 1 ) ).onEvent( event );
    }
}
